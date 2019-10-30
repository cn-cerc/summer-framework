package cn.cerc.ui.sample;

import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.IPage;
import cn.cerc.ui.page.UIPagePhone;
import cn.cerc.ui.phone.Block101;
import cn.cerc.ui.phone.Block102;
import cn.cerc.ui.phone.Block103;
import cn.cerc.ui.phone.Block104;
import cn.cerc.ui.phone.Block105;
import cn.cerc.ui.phone.Block106;
import cn.cerc.ui.phone.Block107;
import cn.cerc.ui.phone.Block108;
import cn.cerc.ui.phone.Block109;
import cn.cerc.ui.phone.Block110;
import cn.cerc.ui.phone.Block111;
import cn.cerc.ui.phone.Block112;
import cn.cerc.ui.phone.Block113;
import cn.cerc.ui.phone.Block114;
import cn.cerc.ui.phone.Block115;
import cn.cerc.ui.phone.Block116;
import cn.cerc.ui.phone.Block117;
import cn.cerc.ui.phone.Block118;
import cn.cerc.ui.phone.Block119;
import cn.cerc.ui.phone.Block120;
import cn.cerc.ui.phone.Block121;
import cn.cerc.ui.phone.Block123;
import cn.cerc.ui.phone.Block124;
import cn.cerc.ui.phone.Block125;
import cn.cerc.ui.phone.Block126;
import cn.cerc.ui.phone.Block127;
import cn.cerc.ui.phone.Block201;
import cn.cerc.ui.phone.Block301;
import cn.cerc.ui.phone.Block302;
import cn.cerc.ui.phone.Block303;
import cn.cerc.ui.phone.Block304;
import cn.cerc.ui.phone.Block305;
import cn.cerc.ui.phone.Block306;
import cn.cerc.ui.phone.Block307;
import cn.cerc.ui.phone.Block401;
import cn.cerc.ui.phone.Block402;
import cn.cerc.ui.phone.Block601;
import cn.cerc.ui.phone.Block602;
import cn.cerc.ui.phone.Block603;
import cn.cerc.ui.phone.Block604;
import cn.cerc.ui.phone.Block901;
import cn.cerc.ui.phone.Block902;
import cn.cerc.ui.phone.Block991;
import cn.cerc.ui.phone.Block992;

public class FrmPhoneSample extends AbstractForm {

    @Override
    public IPage execute() throws Exception {
        UIPagePhone page = new UIPagePhone(this);

        new Block101(page.getContent());
        new Block102(page.getContent());
        new Block103(page.getContent());
        new Block104(page.getContent());
        new Block105(page.getContent());
        new Block106(page.getContent());
        new Block107(page.getContent());
        new Block108(page.getContent());
        new Block109(page.getContent());
        new Block110(page.getContent());
        new Block111(page.getContent());
        new Block112(page.getContent());
        new Block113(page.getContent());
        new Block114(page.getContent());
        new Block115(page.getContent());
        new Block116(page.getContent());
        new Block117(page.getContent());
        new Block118(page.getContent());
        new Block119(page.getContent());
        new Block120(page.getContent());
        new Block121(page.getContent());
        new Block123(page.getContent());
        new Block124(page.getContent());
        new Block125(page.getContent());
        new Block126(page.getContent());
        new Block127(page.getContent());

        new Block201(page.getContent());

        new Block301(page.getContent());
        new Block302(page.getContent());
        new Block303(page.getContent());
        new Block304(page.getContent());
        new Block305(page.getContent());
        new Block306(page.getContent());
        new Block307(page.getContent());

        new Block401(page.getContent());
        new Block402(page.getContent());

        new Block601(page.getContent());
        new Block602(page.getContent());
        new Block603(page.getContent());
        new Block604(page.getContent());

        new Block901(page.getContent());
        new Block902(page.getContent());

        new Block991(page.getContent());
        Block992 b992 = new Block992(page.getContent());
        b992.addButton("进入系统");
        b992.addButton("进入系统");
        b992.addButton("进入系统");
        b992.addButton("进入系统");
        return page;
    }

    @Override
    public boolean logon() {
        return true;
    }
}
