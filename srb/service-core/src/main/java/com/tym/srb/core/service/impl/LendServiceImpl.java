package com.tym.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tym.common.exception.BusinessException;
import com.tym.srb.core.enums.LendStatusEnum;
import com.tym.srb.core.enums.ReturnMethodEnum;
import com.tym.srb.core.enums.TransTypeEnum;
import com.tym.srb.core.hfb.HfbConst;
import com.tym.srb.core.hfb.RequestHelper;
import com.tym.srb.core.mapper.BorrowerMapper;
import com.tym.srb.core.mapper.LendMapper;
import com.tym.srb.core.mapper.UserAccountMapper;
import com.tym.srb.core.mapper.UserInfoMapper;
import com.tym.srb.core.pojo.bo.TransFlowBO;
import com.tym.srb.core.pojo.entity.*;
import com.tym.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.tym.srb.core.pojo.vo.BorrowerDetailVO;
import com.tym.srb.core.service.*;
import com.tym.srb.core.util.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author tym
 * @since 2022-12-18
 */
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Resource
    private DictService dictService;
    @Resource
    private BorrowerMapper borrowerMapper;
    @Resource
    private BorrowerService borrowerService;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private LendItemService lendItemService;
    @Resource
    private TransFlowService transFlowService;
    @Resource
    private LendReturnService lendReturnService;
    @Resource
    private LendItemReturnService lendItemReturnService;

    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        Lend lend = new Lend();
        lend.setUserId(borrowInfo.getUserId());
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setLendNo(LendNoUtils.getLendNo());
        lend.setTitle(borrowInfoApprovalVO.getTitle());
        lend.setAmount(borrowInfo.getAmount());
        lend.setPeriod(borrowInfo.getPeriod());
        lend.setLendYearRate(borrowInfoApprovalVO.getLendYearRate().divide(new BigDecimal(100)));
        lend.setServiceRate(borrowInfoApprovalVO.getServiceRate().divide(new BigDecimal(100)));
        lend.setReturnMethod(borrowInfo.getReturnMethod());
        lend.setLowestAmount(new BigDecimal(100));
        lend.setInvestAmount(new BigDecimal(0));
        lend.setInvestNum(0);
        lend.setPublishDate(LocalDateTime.now());

        DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate lendStartDate = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate());
        lend.setLendStartDate(lendStartDate);

        LocalDate lendEndDate = lendStartDate.plusMonths(borrowInfo.getPeriod());
        lend.setLendEndDate(lendEndDate);

        lend.setLendInfo(borrowInfoApprovalVO.getLendInfo());
        BigDecimal expectAmount = lend.getServiceRate()
                .divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN)
                .multiply(new BigDecimal(lend.getPeriod()))
                .multiply(lend.getAmount());
        lend.setExpectAmount(expectAmount);
        lend.setRealAmount(new BigDecimal(0));
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());
        lend.setCheckTime(LocalDateTime.now());
        lend.setCheckAdminId(1L);

        baseMapper.insert(lend);
    }

    @Override
    public List<Lend> selectList() {

        List<Lend> lendList = baseMapper.selectList(null);
        lendList.forEach(lend -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod",lend.getReturnMethod());
            String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
            lend.getParam().put("returnMethod",returnMethod);
            lend.getParam().put("status",status);
        });
        return lendList;
    }

    @Override
    public Map<String, Object> getLendDetail(Long id) {
        Lend lend = baseMapper.selectById(id);
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod",lend.getReturnMethod());
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        lend.getParam().put("returnMethod",returnMethod);
        lend.getParam().put("status",status);

        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id",lend.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        HashMap<String, Object> result = new HashMap<>();
        result.put("lend",lend);
        result.put("borrower",borrowerDetailVO);
        return result;

    }

    @Override
    public BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod) {
        BigDecimal interestCount;
        if (returnMethod.intValue() == ReturnMethodEnum.ONE.getMethod()){
            interestCount = Amount1Helper.getInterestCount(invest, yearRate, totalmonth);
        } else if (returnMethod.intValue() == ReturnMethodEnum.TWO.getMethod()) {
            interestCount = Amount2Helper.getInterestCount(invest, yearRate, totalmonth);
        }else if (returnMethod.intValue() == ReturnMethodEnum.THREE.getMethod()) {
            interestCount = Amount3Helper.getInterestCount(invest, yearRate, totalmonth);
        }else{
            interestCount = Amount4Helper.getInterestCount(invest, yearRate, totalmonth);
        }
        return interestCount;
    }

    @Override
    public void makeLoan(Long id) {
        //获取标的信息
        Lend lend = baseMapper.selectById(id);
        //调用hfb放款接口
        HashMap<String, Object> map = new HashMap<>();
        map.put("agentId", HfbConst.AGENT_ID);
        map.put("agentProjectCode",lend.getLendNo());
        map.put("agentBillNo",LendNoUtils.getLoanNo());
        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal(12),8,BigDecimal.ROUND_DOWN);
        BigDecimal realAmount = lend.getInvestAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));
        map.put("mchFee",monthRate.multiply(realAmount));
        map.put("timestamp", RequestHelper.getTimestamp());
        map.put("sing",RequestHelper.getSign(map));

        JSONObject result = RequestHelper.sendRequest(map, HfbConst.MAKE_LOAD_URL);
        if (!"0000".equals(result.getString("resultCode"))){
            throw new BusinessException(result.getString("resultMsg"));
        }

        //标的状态和标的平台收益，更新标的相关信息
        lend.setRealAmount(realAmount);
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setPaymentTime(LocalDateTime.now());
        baseMapper.updateById(lend);
        //给借款账号转入金额
        Long userId = lend.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        BigDecimal voteAmt = new BigDecimal(result.getString("voteAmt"));
        userAccountMapper.updateAccount(bindCode,voteAmt,new BigDecimal(0));
        //增加借款交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                result.getString("agentBillNo"),
                bindCode,
                voteAmt,
                TransTypeEnum.BORROW_BACK,
                "项目放款，项目编号："+lend.getLendNo()+"，项目名称"+lend.getTitle()
        );
        transFlowService.saveTransFlow(transFlowBO);
        //解冻并扣除投资人资金
        //  获取标的下的投资列表
        List<LendItem> lendItemList = lendItemService.selectByLendId(id,1);
        lendItemList.stream().forEach(lendItem -> {
            Long investUserId = lendItem.getInvestUserId();
            UserInfo investUserInfo = userInfoMapper.selectById(investUserId);
            String investBindCode = investUserInfo.getBindCode();
            BigDecimal investAmount = lendItem.getInvestAmount().negate();
            userAccountMapper.updateAccount(investBindCode,new BigDecimal(0),investAmount);
            //增加投资人交易流水
            TransFlowBO investTransFlowBO = new TransFlowBO(
                    LendNoUtils.getTransNo(),
                    investBindCode,
                    investAmount,
                    TransTypeEnum.INVEST_UNLOCK,
                    "项目放款，项目编号："+lend.getLendNo()+"，项目名称"+lend.getTitle()
            );
            transFlowService.saveTransFlow(investTransFlowBO);
        });
        //生成借款人还款计划和出借人回款计划
        this.repaymentPlan(lend);
    }

    //还款计划
    private void repaymentPlan(Lend lend){
        ArrayList<LendReturn> lendReturnList = new ArrayList<>();
        int len = lend.getPeriod().intValue();
        for (int i = 0; i < len; i++) {
            LendReturn lendReturn = new LendReturn();
            //填充基本属性
            lendReturn.setReturnNo(LendNoUtils.getReturnNo());
            lendReturn.setLendId(lend.getId());
            lendReturn.setBorrowInfoId(lend.getBorrowInfoId());
            lendReturn.setUserId(lend.getUserId());
            lendReturn.setAmount(lend.getAmount());
            lendReturn.setBaseAmount(lend.getInvestAmount());
            lendReturn.setLendYearRate(lend.getLendYearRate());
            lendReturn.setCurrentPeriod(i);//当前期数
            lendReturn.setReturnMethod(lend.getReturnMethod());

            lendReturn.setFee(new BigDecimal("0"));
            lendReturn.setReturnDate(lend.getLendStartDate().plusMonths(i)); //第二个月开始还款
            lendReturn.setOverdue(false);

            //判断是否最后一期还款
            if (i==len){
                //最后一期
                lendReturn.setLast(true);
            }else {
                lendReturn.setLast(false);
            }
            //设置还款状态
            lendReturn.setStatus(0);
            //将还款对象加入还款计划列表
            lendReturnList.add(lendReturn);
        }
        //批量保存还款计划
        lendReturnService.saveBatch(lendReturnList);
        //生成期数和还款记录的id对应的键值对集合
        Map<Integer, Long> lendReturnMap = lendReturnList.stream().collect(
                Collectors.toMap(LendReturn::getCurrentPeriod, LendReturn::getId)
        );
        //创建所以投资的所以回款记录列表
        ArrayList<LendItemReturn> lendItemReturnAllList = new ArrayList<>();
        //获取当前标的下的所有的已支付的投资
        List<LendItem> lendItemList = lendItemService.selectByLendId(lend.getId(), 1);
        for (LendItem lendItem : lendItemList) {
            //根据投资记录的id调用回款计划生成的方法，得到当前这笔投资的回款计划列表
            List<LendItemReturn> lendItemReturnList = this.returnInvest(lendItem.getId(), lendReturnMap, lend);
            // 将当前这笔投资的回款计划列表  放入  所有投资的所有回款记录列表
            lendItemReturnAllList.addAll(lendItemReturnList);
        }

        //遍历还款记录列表
        for (LendReturn lendReturn : lendReturnList) {

            //通过filter、map、reduce将相关期数的回款数据过滤出来
            //将当前期数的所有投资人的数据相加，就是当前期数的所有投资人的回款数据（本金、利息、总金额）
            BigDecimal sumPrincipal = lendItemReturnAllList.stream()
                    .filter(item -> item.getLendReturnId().longValue() == lendReturn.getId().longValue())
                    .map(LendItemReturn::getPrincipal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal sumInterest = lendItemReturnAllList.stream()
                    .filter(item -> item.getLendReturnId().longValue() == lendReturn.getId().longValue())
                    .map(LendItemReturn::getInterest)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal sumTotal = lendItemReturnAllList.stream()
                    .filter(item -> item.getLendReturnId().longValue() == lendReturn.getId().longValue())
                    .map(LendItemReturn::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            //将计算出的数据填充入还款计划记录：设置本金、利息、总金额
            lendReturn.setPrincipal(sumPrincipal);
            lendReturn.setInterest(sumInterest);
            lendReturn.setTotal(sumTotal);
        }

        //批量更新还款计划列表
        lendReturnService.updateBatchById(lendReturnList);

    }

    //回款计划(针对某一笔的投资的回款计划)
    private List<LendItemReturn> returnInvest(Long lendItemId,Map<Integer,Long> lendReturnMap,Lend lend){
        //获取当前投资记录信息
        LendItem lendItem = lendItemService.getById(lendItemId);
        //调用工具类计算还款本金和利息，存储为集合
        // {key：value}
        // {期数：本金|利息}
        BigDecimal amount = lendItem.getInvestAmount();
        BigDecimal yearRate = lendItem.getLendYearRate();
        Integer totalMonth = lend.getPeriod();

        Map<Integer, BigDecimal> mapInterest = null;  //还款期数 -> 利息
        Map<Integer, BigDecimal> mapPrincipal = null; //还款期数 -> 本金

        //根据还款方式计算本金和利息
        if (lend.getReturnMethod().intValue() == ReturnMethodEnum.ONE.getMethod()) {
            //利息
            mapInterest = Amount1Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            //本金
            mapPrincipal = Amount1Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else if (lend.getReturnMethod().intValue() == ReturnMethodEnum.TWO.getMethod()) {
            mapInterest = Amount2Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            mapPrincipal = Amount2Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else if (lend.getReturnMethod().intValue() == ReturnMethodEnum.THREE.getMethod()) {
            mapInterest = Amount3Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            mapPrincipal = Amount3Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else {
            mapInterest = Amount4Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            mapPrincipal = Amount4Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        }

        //创建回款计划列表
        List<LendItemReturn> lendItemReturnList = new ArrayList<>();

        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            Integer currentPeriod = entry.getKey();//当前期数
            // 根据当前期数，获取还款计划的id
            Long lendReturnId = lendReturnMap.get(currentPeriod);

            //创建回款计划记录
            LendItemReturn lendItemReturn = new LendItemReturn();
            //将还款记录关联到回款记录
            lendItemReturn.setLendReturnId(lendReturnId);
            //设置回款记录的基本属性
            lendItemReturn.setLendItemId(lendItemId);
            lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
            lendItemReturn.setLendId(lendItem.getLendId());
            lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
            lendItemReturn.setLendYearRate(lend.getLendYearRate());
            lendItemReturn.setCurrentPeriod(currentPeriod);
            lendItemReturn.setReturnMethod(lend.getReturnMethod());
            //计算回款本金、利息和总额（注意最后一个月的计算）
            if (currentPeriod.intValue() == lend.getPeriod().intValue()) {//最后一期
                //本金
                BigDecimal sumPrincipal = lendItemReturnList.stream()
                        .map(LendItemReturn::getPrincipal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal lastPrincipal = lendItem.getInvestAmount().subtract(sumPrincipal);
                lendItemReturn.setPrincipal(lastPrincipal);

                //利息
                BigDecimal sumInterest = lendItemReturnList.stream()
                        .map(LendItemReturn::getInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal lastInterest = lendItem.getExpectAmount().subtract(sumInterest);
                lendItemReturn.setInterest(lastInterest);

            } else {//非最后一期
                //本金
                lendItemReturn.setPrincipal(mapPrincipal.get(currentPeriod));
                //利息
                lendItemReturn.setInterest(mapInterest.get(currentPeriod));
            }

            //回款总金额
            lendItemReturn.setTotal(lendItemReturn.getPrincipal().add(lendItemReturn.getInterest()));

            //设置回款状态和是否逾期等其他属性
            lendItemReturn.setFee(new BigDecimal("0"));
            lendItemReturn.setReturnDate(lend.getLendStartDate().plusMonths(currentPeriod));
            lendItemReturn.setOverdue(false);
            lendItemReturn.setStatus(0);

            //将回款记录放入回款列表
            lendItemReturnList.add(lendItemReturn);
        }

        //批量保存
        lendItemReturnService.saveBatch(lendItemReturnList);

        return lendItemReturnList;

    }

}
