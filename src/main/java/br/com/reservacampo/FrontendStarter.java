package br.com.reservacampo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class FrontendStarter {

    private static final Logger logger = LoggerFactory.getLogger(FrontendStarter.class);

    @EventListener(ApplicationReadyEvent.class)
    public void startFrontend() {
        try {
            logger.info("Tentando iniciar o frontend (Vite) automaticamente...");
            
            File frontendDir = new File("src/main/frontend");
            
            if (!frontendDir.exists() || !frontendDir.isDirectory()) {
                logger.warn("Pasta src/main/frontend não encontrada. Não foi possível iniciar o Vite.");
                return;
            }

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "npm run dev");
            pb.directory(frontendDir);
            pb.inheritIO();
            
            pb.start();
            
            logger.info("Frontend (Vite) foi iniciado! Acesse http://localhost:5173");

        } catch (IOException e) {
            logger.error("Erro ao tentar iniciar o frontend automaticamente", e);
        }
    }
}

