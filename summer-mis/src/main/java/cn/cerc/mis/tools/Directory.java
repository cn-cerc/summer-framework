package cn.cerc.mis.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Directory {
    private List<String> paths = new ArrayList<>();
    private List<String> files = new ArrayList<>();
    private FileFilter onFilter;

    public int list(String path) {
        paths.clear();
        files.clear();
        getAllFilePaths(new File(path));
        return paths.size() + files.size();
    }

    private void getAllFilePaths(File filePath) {
        File[] items = filePath.listFiles();
        if (items == null) {
            return;
        }
        for (File f : items) {
            if (f.isDirectory()) {
                paths.add(f.getPath());
                getAllFilePaths(f);
            } else {
                if (onFilter == null ? true : onFilter.check((f))) {
                    this.files.add(f.getPath());
                }
            }
        }
        return;
    }

    public FileFilter getOnFilter() {
        return onFilter;
    }

    public void setOnFilter(FileFilter onFilter) {
        this.onFilter = onFilter;
    }

    public List<String> getPaths() {
        return paths;
    }

    public List<String> getFiles() {
        return files;
    }
}
