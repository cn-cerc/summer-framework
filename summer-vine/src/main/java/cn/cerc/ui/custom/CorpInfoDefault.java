package cn.cerc.ui.custom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.services.BookInfoRecord;
import cn.cerc.mis.services.MemoryBookInfo;
import cn.cerc.ui.core.ICorpInfo;

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class CorpInfoDefault implements ICorpInfo {
    private Map<String, String> items = new ConcurrentHashMap<>();

    @Override
    public String getShortName(IHandle handle) {
        if (items.containsKey(handle.getCorpNo()))
            return items.get(handle.getCorpNo());

        BookInfoRecord item = MemoryBookInfo.get(handle, handle.getCorpNo());
        items.put(handle.getCorpNo(), item.getShortName());
        return item.getShortName();
    }

}
