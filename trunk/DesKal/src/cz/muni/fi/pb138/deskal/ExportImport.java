package cz.muni.fi.pb138.deskal;

import java.io.File;

public interface ExportImport {

    public void importFromHCal(File file);

    public void importFromICal(File file);

    public void exportToHCal(File file);

    public void exportToICal(File file);
}
