package com.motorist.businesslogic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EntityCarAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "action_log", length=2000)
    private String actionLog;

    @Column (name = "type_user", length=255)
    private String typeUser;

    @Column (name = "digitalSignature" , length=2048)
    private String digitalSignature;

    @Column (name = "configuration" , length=2000)
    private String configuration;

    public EntityCarAudit() {}

    public EntityCarAudit(
        final String actionLog, final String typeUser,
        final String digitalSignature, final String configuration)
    {
        this.actionLog = actionLog;
        this.typeUser = typeUser;
        this.digitalSignature = digitalSignature;
        this.configuration = configuration;
    }

    public Long getId() {
        return id;
    }

    public String getActionLog() {
        return actionLog;
    }
    public String getDS() {
        return digitalSignature;
    }
    public String getConfiguration() {
        return configuration;
    }
    public String getUser() {
        return typeUser;
    }

    public void setActionLog(String actionLog) {
        this.actionLog = actionLog;
    }
}
