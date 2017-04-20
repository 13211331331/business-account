select t.serialno 合同号, t.objectno , t.paydate 合同时间, t.payprincipalamt 还款金额
  from ACCT_PAYMENT_SCHEDULE t where  rownum<60000