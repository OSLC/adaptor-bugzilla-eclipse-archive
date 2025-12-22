package se.kth.md.it.bcm.bugzilla;

import java.util.Date;

public class Bug {
    private int id;
    private String summary;
    private String status;
    private String product;
    private String component;
    private String version;
    private String priority;
    private String platform;
    private String operatingSystem;
    private String assignedTo;
    private Date creationTime;
    private Date lastChangeTime;

    public int getID() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public Date getCreationTime() { return creationTime; }
    public void setCreationTime(Date creationTime) { this.creationTime = creationTime; }

    public Date getLastChangeTime() { return lastChangeTime; }
    public void setLastChangeTime(Date lastChangeTime) { this.lastChangeTime = lastChangeTime; }
}
