package com.nsi.domain.core;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "contentotp")
public class ContentOTP implements Serializable {
    private Long  id;
    private String content;
    private String subject;
    private String typeotp;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contentotp_generator")
    @SequenceGenerator(name="contentotp_generator", sequenceName = "contentotp_id_seq", allocationSize=1)
    @Column(name="id")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="content", columnDefinition = "text")
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Column(name="subject")
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(name="typeotp")
    public String getTypeotp() {
        return typeotp;
    }
    public void setTypeotp(String typeotp) {
        this.typeotp = typeotp;
    }

}
