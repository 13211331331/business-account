package cn.billionsfinance.businessaccount.utils;


import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanlin.huang on 2017/4/10.
 */
public class ExportUtil {

    public static void main(String[] args){

        String sql = "SELECT ZPD.KEEPACCOUNTSDATE as 记账日期,\n" +
                "       ZPD.ACCORDDATE as 统计日期,\n" +
                "       ZPD.SERIALNO as 合同号,\n" +
                "       ZPD.CLIENTNAME as 客户姓名,\n" +
                "       ZPD.REGISTRATIONDATE as 注册日期,\n" +
                "       ZPD.MATURITYDATE as 合同到期日,\n" +
                "       ZPD.SNO as 门店代码,\n" +
                "       ZPD.RNO as 商户代码,\n" +
                "       ZPD.SA_ID,\n" +
                "       ZPD.PRODUCTCATEGORY as 商品范畴,\n" +
                "       ZPD.SURETYPE as 客户渠道,\n" +
                "       ZPD.BUSINESSMODEL as 业务模式,\n" +
                "       ZPD.PRODUCTID as 合同类型,\n" +
                "       ZPD.SUBPRODUCTTYPE as 产品子类型,\n" +
                "       ZPD.PROVINCE as 省份,\n" +
                "       ZPD.CITY as 城市,\n" +
                "       ZPD.CITYCODE as 城市代码,\n" +
                "     (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.CREDITPERSON) as 资金方, \n" +
                "       ZPD.CONTRACTLIFE as 合同轨迹,\n" +
                "       ZPD.CDATE as 强制日期,\n" +
                "       ZPD.PAYDATE as 应还日期,\n" +
                "       NVL(ZPD.SEQID, 0) AS 序号,\n" +
                "       CASE WHEN ZPD.CANCELTYPE= 'Y' THEN '是'    ELSE '否' END AS 是否取消分期期次,\n" +
                "       ZPD.DELIVERYDATE as 转让日,\n" +
                "       ZPD.DCDATE as 代偿日,\n" +
                "       ZPD.SHDATE as 赎回日,\n" +
                "       ZPD.TBDATE as 保险投保日,\n" +
                "       ZPD.LPDATE as 保险理赔日,\n" +
                "       (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.DEBOURS) as 代垫方, \n" +
                "     (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.ASSETBELONG) as 资产所属方, \n" +
                "     (SELECT ITEMNAME  FROM creditperson_code WHERE CODENO= ZPD.GUARANTEEPARTY) as 保证方, \n" +
                "     CASE WHEN ZPD.PAYTYPE= '0055' THEN '提前还款' WHEN ZPD.PAYTYPE= '0055H'  THEN '提前还款' ELSE '正常还款' END AS 还款类型,\n" +
                "       NVL(ZPD.PAYPRINCIPALAMT, 0.00) AS 本金, \n" +
                "       NVL(ZPD.PAYINTEAMT, 0.00) AS 利息,\n" +
                "       NVL(ZPD.A11,0.00) AS 印花税 ,\n" +
                "       NVL(ZPD.PAYAMT, 0.00) AS 总额,\n" +
                "       ZPD.APPROVETIME as 审核日期,\n" +
                "       CASE WHEN ZPD.APPROVESTATUS= '0' THEN '未审核'  WHEN ZPD.APPROVESTATUS= 0  THEN '未审核'   ELSE '已审核' END AS 审核状态,\n" +
                "       CASE WHEN ZPD.PAYSTATUS= '0' THEN '未支付'  WHEN ZPD.PAYSTATUS= 0  THEN '未支付'   ELSE '已支付' END AS 支付状态,\n" +
                "       CASE WHEN ZPD.KEEPACCOUNTSSTATUS= '1' THEN '已记账'  WHEN ZPD.KEEPACCOUNTSSTATUS= '2' THEN '已撤销'   WHEN ZPD.KEEPACCOUNTSSTATUS= '0' THEN '未记账'  ELSE  ZPD.KEEPACCOUNTSSTATUS  END   as 记账状态\n" +
                "  FROM ZJJS_BELONG_XT ZPD\n" +
                " where 1 = 1\n" +
                "   AND ZPD.PAYDATE > '2017/03/28'\n" +
                "   AND ZPD.PAYDATE <= '2017/04/01'\n" +
                //"   AND ZPD.PAYTYPE IN('0055','0055H')";
                "   AND ZPD.PAYTYPE NOT IN('0055','0055H')";

        //System.out.println(sql);

        String countSql = "SELECT COUNT(1) SUM FROM ("+sql+")";
        ConsoleProgressBar CP1 = new ConsoleProgressBar(0, 2, 50, '#','=');
        CP1.show(1,"初始数据库...");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Long count = 0l;
        ResultSetMetaData rsmd = null;
        try {
            conn = JdbcUtil.getConnection();
            stmt = conn.createStatement();
            CP1.show(2,"初始数据库完成");

            ConsoleProgressBar CP2 = new ConsoleProgressBar(0, 3, 50, '#','=');
            CP2.show(1,"获取总记录数...");
            rs = stmt.executeQuery(countSql);
            while (rs.next()){
                count = rs.getLong("SUM");
            }
            CP2.show(2,"获取总记录数成功");
            CP2.show(3,"总记录数：" + count);

            JdbcUtil.close(rs);

            rs = stmt.executeQuery(sql);
            rsmd = rs.getMetaData();

            List<String> list = new ArrayList<String>();
            for(int i=1;i<=rsmd.getColumnCount();i++){
                String name = rsmd.getColumnName(i);
                list.add(name);
            }

            ExportExcel2007 exportExcel2007 = new ExportExcel2007();

            try {
                exportExcel2007.writeExcelToFile("D:\\doc", "test111",count, "导出信托回款", list,rs);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(stmt);
            JdbcUtil.close(conn);

        }







    }




}
