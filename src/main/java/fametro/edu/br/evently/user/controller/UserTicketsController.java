package fametro.edu.br.evently.user.controller;

import fametro.edu.br.evently.event.dto.MyTicketsDTO;
import fametro.edu.br.evently.event.service.EventSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user/tickets")
@RequiredArgsConstructor
public class UserTicketsController {

    private final EventSubscriptionService subscriptionService;

    @GetMapping
    public String myTickets(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<MyTicketsDTO> allTickets = subscriptionService.listAllMyTickets(userDetails.getUsername());
        
        List<MyTicketsDTO> upcomingTickets = allTickets.stream()
                .filter(t -> !t.isFinished())
                .toList();
                
        List<MyTicketsDTO> finishedTickets = allTickets.stream()
                .filter(MyTicketsDTO::isFinished)
                .toList();

        model.addAttribute("upcomingTickets", upcomingTickets);
        model.addAttribute("finishedTickets", finishedTickets);
        
        return "user/my-tickets";
    }
}
