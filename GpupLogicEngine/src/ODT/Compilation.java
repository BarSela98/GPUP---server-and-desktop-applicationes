package ODT;

public class Compilation {
    private int priceOfCompilation;
    private String sourceFolder;
    private String targetFolder;
    public Compilation(int priceOfCompilation, String sourceFolder, String targetFolder) {
        this.priceOfCompilation = priceOfCompilation;
        this.sourceFolder = sourceFolder;
        this.targetFolder = targetFolder;
    }
    public int getPriceOfCompilation() {
        return priceOfCompilation;
    }
    public void setPriceOfCompilation(int priceOfCompilation) {
        this.priceOfCompilation = priceOfCompilation;
    }
    public String getSourceFolder() {
        return sourceFolder;
    }
    public void setSourceFolder(String sourceFolder) {
        this.sourceFolder = sourceFolder;
    }
    public String getTargetFolder() {
        return targetFolder;
    }
    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
    }
}
