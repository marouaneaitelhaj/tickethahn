package com.example.service;

import com.example.entities.Ticket;
import com.example.entities.User;
import com.example.network.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TicketService {

    private static final String TICKETS_ENDPOINT        = "http://localhost:8080/api/v1/tickets";
    private static final String UPDATE_STATUS_ENDPOINT  = "http://localhost:8080/api/v1/tickets/change-status";
    private static final String UPDATE_TICKET_ENDPOINT  = "http://localhost:8080/api/v1/tickets/";
    private static final String COMMENTS_ENDPOINT       = "http://localhost:8080/api/v1/comments";

    private ApiClient apiClient = ApiClient.getInstance();

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String response = apiClient.doGetRequest(TICKETS_ENDPOINT, true);
        if (response.startsWith("Error:")) {
            return tickets;
        }
        try {
            JSONArray arr = new JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Ticket ticket = new Ticket();
                ticket.setId(obj.optString("id", "N/A"));
                ticket.setTitle(obj.optString("title", "N/A"));
                ticket.setDescription(obj.optString("description", ""));
                ticket.setPriority(obj.optString("priority", "N/A"));
                ticket.setCategory(obj.optString("category", "N/A"));
                ticket.setStatus(obj.optString("status", "N/A"));
                JSONObject assignedToObj = obj.optJSONObject("assignedTo");
                if (assignedToObj != null) {
                    User assignedTo = new User();
                    assignedTo.setUsername(assignedToObj.optString("username", "N/A"));
                    assignedTo.setId(assignedToObj.optString("id", "N/A"));
                    ticket.setAssignedTo(assignedTo);
                } else {
                    User assignedTo = new User();
                    assignedTo.setUsername("N/A");
                    ticket.setAssignedTo(assignedTo);
                }
                tickets.add(ticket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tickets;
    }

    public boolean updateTicketStatus(String ticketId, String newStatus) {
        String json = "{\"id\":\"" + ticketId + "\", \"status\":\"" + newStatus + "\"}";
        String response = apiClient.doPostRequest(UPDATE_STATUS_ENDPOINT, json, true);
        System.out.println(response);
        return !response.startsWith("Error:");
    }

    public boolean updateTicket(Ticket ticket) {
        String json = "{" +
                "\"id\":\"" + ticket.getId() + "\"," +
                "\"title\":\"" + ticket.getTitle() + "\"," +
                "\"description\":\"" + ticket.getDescription() + "\"," +
                "\"priority\":\"" + ticket.getPriority() + "\"," +
                "\"category\":\"" + ticket.getCategory() + "\"," +
                "\"status\":\"" + ticket.getStatus() + "\"," +
                "\"assignedTo_id\":\"" + ticket.getAssignedTo().getId() + "\"" +
                "}";
        String response = apiClient.doPutRequest(UPDATE_TICKET_ENDPOINT + ticket.getId(), json, true);
        System.out.println(response);
        return !response.startsWith("Error:");
    }

    // Updated addComment to include CommentReq fields: ticket_id, user_id, message.
    public boolean addComment(String ticketId, String userId, String commentText) {
        String json = "{\"ticket_id\":\"" + ticketId + "\", \"user_id\":\"" + userId + "\", \"message\":\"" + commentText + "\"}";
        String response = apiClient.doPostRequest(COMMENTS_ENDPOINT, json, true);
        return !response.startsWith("Error:");
    }
}
