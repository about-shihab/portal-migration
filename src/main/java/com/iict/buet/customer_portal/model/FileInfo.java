package com.iict.buet.customer_portal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "FILE_INFO")
@Getter
@Setter
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name; // file name

    @Column(name = "TITLE")
    private String title; // e.g. bill copy

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @Column(name = "FILE_SIZE")
    private Long size;

    @Column(name = "FILE_URL")
    private String url;

    @Column(name = "FILE_PATH")
    private String path;

    @Column(name = "EXTENSION")
    private String extension;

    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
}

