package fametro.edu.br.evently.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import fametro.edu.br.evently.event.model.Event;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Double totalRevenue;
    private Long totalTicketsSold;
    private Long activeEventsCount;
    private Long upcomingEventsCount;
    private List<Event> upcomingEvents;
    
    // Percentages for trends (mocked for now or calculated if historical data exists)
    private Double revenueTrend;
    private Double ticketsTrend;
}
