
SELECT ZPD.KEEPACCOUNTSDATE as ��������,
       ZPD.ACCORDDATE as ͳ������,
       ZPD.SERIALNO as ��ͬ��,
       ZPD.CLIENTNAME as �ͻ�����,
       ZPD.REGISTRATIONDATE as ע������,
       ZPD.MATURITYDATE as ��ͬ������,
       ZPD.SNO as �ŵ����,
       ZPD.RNO as �̻�����,
       ZPD.SA_ID,
       ZPD.PRODUCTCATEGORY as ��Ʒ����,
       ZPD.SURETYPE as �ͻ�����,
       ZPD.BUSINESSMODEL as ҵ��ģʽ,
       ZPD.PRODUCTID as ��ͬ����,
       ZPD.SUBPRODUCTTYPE as ��Ʒ������,
       ZPD.PROVINCE as ʡ��,
       ZPD.CITY as ����,
       ZPD.CITYCODE as ���д���,
     (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.CREDITPERSON) as �ʽ�, 
       ZPD.CONTRACTLIFE as ��ͬ�켣,
       ZPD.CDATE as ǿ������,
       ZPD.PAYDATE as Ӧ������,
       NVL(ZPD.SEQID, 0) AS ���,
       CASE WHEN ZPD.CANCELTYPE= 'Y' THEN '��'    ELSE '��' END AS �Ƿ�ȡ�������ڴ�,
       ZPD.DELIVERYDATE as ת����,
       ZPD.DCDATE as ������,
       ZPD.SHDATE as �����,
       ZPD.TBDATE as ����Ͷ����,
       ZPD.LPDATE as ����������,
       (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.DEBOURS) as ���淽, 
     (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.ASSETBELONG) as �ʲ�������, 
     (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.GUARANTEEPARTY) as ��֤��, 
     CASE WHEN ZPD.PAYTYPE= '0055' THEN '��ǰ����' WHEN ZPD.PAYTYPE= '0055H'  THEN '��ǰ����' ELSE '��������' END AS ��������,
       NVL(ZPD.PAYPRINCIPALAMT, 0.00) AS ����, 
       NVL(ZPD.PAYINTEAMT, 0.00) AS ��Ϣ,
       NVL(ZPD.A11,0.00) AS ӡ��˰ ,
       NVL(ZPD.PAYAMT, 0.00) AS �ܶ�,
       ZPD.APPROVETIME as �������,
       CASE WHEN ZPD.APPROVESTATUS= '0' THEN 'δ���'  WHEN ZPD.APPROVESTATUS= 0  THEN 'δ���'   ELSE '�����' END AS ���״̬,
       CASE WHEN ZPD.PAYSTATUS= '0' THEN 'δ֧��'  WHEN ZPD.PAYSTATUS= 0  THEN 'δ֧��'   ELSE '��֧��' END AS ֧��״̬,
       CASE WHEN ZPD.KEEPACCOUNTSSTATUS= '1' THEN '�Ѽ���'  WHEN ZPD.KEEPACCOUNTSSTATUS= '2' THEN '�ѳ���'   WHEN ZPD.KEEPACCOUNTSSTATUS= '0' THEN 'δ����'  ELSE  ZPD.KEEPACCOUNTSSTATUS  END   as ����״̬
  FROM ZJJS_BELONG_XT ZPD
 where 1 = 1
   AND ZPD.PAYDATE >= '2017/03/30'
   AND ZPD.PAYDATE <= '2017/04/05'

   AND ZPD.PAYTYPE NOT IN('0055','0055H');
   
   
   
   


