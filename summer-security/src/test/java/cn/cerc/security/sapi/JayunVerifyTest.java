package cn.cerc.security.sapi;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

public class JayunVerifyTest {

    private HttpServletRequest request;

    @Test
    /**
     * @param user
     *            应用用户帐号
     * @param realName
     *            用户真实姓名
     * @param idCard
     *            用户身份证号
     */
    public void idCard(String user, String realName, String idCard) {
        JayunVerify api = new JayunVerify(request);
        boolean result = api.idCard(user, realName, idCard);
        if (result) {
            System.out.println("身份证与姓名效验一致性通过");
        } else {
            System.out.println(api.getMessage());
        }
    }

    @Test
    /**
     * @param user
     *            应用用户帐号
     * @param realName
     *            用户真实姓名
     * @param idCard
     *            用户身份证号
     * @param bankCard
     *            用户银行卡号
     * @param mobile
     *            用户手机号码
     */
    public void testBankCard(String user, String realName, String idCard, String bankCard, String mobile) {
        JayunVerify api = new JayunVerify(request);
        boolean result = api.bankCard(user, realName, idCard, bankCard, mobile);
        if (result) {
            System.out.println("银行卡验证成功");
        } else {
            System.out.println(api.getMessage());
        }
    }

}
