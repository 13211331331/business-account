
SELECT ZPD.KEEPACCOUNTSDATE as 记账日期,
       ZPD.ACCORDDATE as 统计日期,
       ZPD.SERIALNO as 合同号,
       ZPD.CLIENTNAME as 客户姓名,
       ZPD.REGISTRATIONDATE as 注册日期,
       ZPD.MATURITYDATE as 合同到期日,
       ZPD.SNO as 门店代码,
       ZPD.RNO as 商户代码,
       ZPD.SA_ID,
       ZPD.PRODUCTCATEGORY as 商品范畴,
       ZPD.SURETYPE as 客户渠道,
       ZPD.BUSINESSMODEL as 业务模式,
       ZPD.PRODUCTID as 合同类型,
       ZPD.SUBPRODUCTTYPE as 产品子类型,
       ZPD.PROVINCE as 省份,
       ZPD.CITY as 城市,
       ZPD.CITYCODE as 城市代码,
     (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.CREDITPERSON) as 资金方, 
       ZPD.CONTRACTLIFE as 合同轨迹,
       ZPD.CDATE as 强制日期,
       ZPD.PAYDATE as 应还日期,
       NVL(ZPD.SEQID, 0) AS 序号,
       CASE WHEN ZPD.CANCELTYPE= 'Y' THEN '是'    ELSE '否' END AS 是否取消分期期次,
       ZPD.DELIVERYDATE as 转让日,
       ZPD.DCDATE as 代偿日,
       ZPD.SHDATE as 赎回日,
       ZPD.TBDATE as 保险投保日,
       ZPD.LPDATE as 保险理赔日,
       (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.DEBOURS) as 代垫方, 
     (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.ASSETBELONG) as 资产所属方, 
     (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.GUARANTEEPARTY) as 保证方, 
     CASE WHEN ZPD.PAYTYPE= '0055' THEN '提前还款' WHEN ZPD.PAYTYPE= '0055H'  THEN '提前还款' ELSE '正常还款' END AS 还款类型,
       NVL(ZPD.PAYPRINCIPALAMT, 0.00) AS 本金, 
       NVL(ZPD.PAYINTEAMT, 0.00) AS 利息,
       NVL(ZPD.A11,0.00) AS 印花税 ,
       NVL(ZPD.PAYAMT, 0.00) AS 总额,
       ZPD.APPROVETIME as 审核日期,
       CASE WHEN ZPD.APPROVESTATUS= '0' THEN '未审核'  WHEN ZPD.APPROVESTATUS= 0  THEN '未审核'   ELSE '已审核' END AS 审核状态,
       CASE WHEN ZPD.PAYSTATUS= '0' THEN '未支付'  WHEN ZPD.PAYSTATUS= 0  THEN '未支付'   ELSE '已支付' END AS 支付状态,
       CASE WHEN ZPD.KEEPACCOUNTSSTATUS= '1' THEN '已记账'  WHEN ZPD.KEEPACCOUNTSSTATUS= '2' THEN '已撤销'   WHEN ZPD.KEEPACCOUNTSSTATUS= '0' THEN '未记账'  ELSE  ZPD.KEEPACCOUNTSSTATUS  END   as 记账状态
  FROM ZJJS_BELONG_XT ZPD
 where 1 = 1
   AND ZPD.PAYDATE >= '2017/03/30'
   AND ZPD.PAYDATE <= '2017/04/05'

   AND ZPD.PAYTYPE NOT IN('0055','0055H');
   
   
   
   


