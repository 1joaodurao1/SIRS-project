package com.motorist.businesslogic.domain;

import jakarta.persistence.*;

@Entity
public class EntityAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "action_log")
    private String actionLog;

    public EntityAudit() {}

    public EntityAudit(
        final String actionLog)
    {
        this.actionLog = actionLog;
    }

    public Long getId() {
        return id;
    }

    public String getActionLog() {
        return actionLog;
    }

    public void setActionLog(String actionLog) {
        this.actionLog = actionLog;
    }
}
