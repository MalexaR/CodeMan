package com.example.CodeMan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScormReporter {

    private static final Logger log = LoggerFactory.getLogger(ScormReporter.class);

    public void registrar(Long partidaId, int porcentaje) {
        log.info("[SCORM] Partida {} finalizada – porcentaje reportado: {}%", partidaId, porcentaje);
    }
}
