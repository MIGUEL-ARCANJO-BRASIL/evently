package fametro.edu.br.evently.event.scheduler;

import fametro.edu.br.evently.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventScheduler {

    private final EventRepository eventRepository;

    /**
     * Verifica a cada hora se existem eventos ATIVOS cuja data já passou.
     * Se encontrados, altera o status para TERMINADO.
     */
    @Scheduled(cron = "0 0 * * * *") // Executa no minuto 0 de cada hora
    public void updateFinishedEvents() {
        log.info("Iniciando tarefa agendada: Atualização de status de eventos expirados...");
        try {
            int updatedCount = eventRepository.updateExpiredEvents();
            if (updatedCount > 0) {
                log.info("Sucesso: {} eventos foram marcados como TERMINADO.", updatedCount);
            } else {
                log.info("Nenhum evento expirado encontrado para atualização.");
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar status de eventos expirados: {}", e.getMessage());
        }
    }
}
