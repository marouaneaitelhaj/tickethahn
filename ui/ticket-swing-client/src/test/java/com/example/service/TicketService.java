package com.example.service;

import com.example.entities.Ticket;
import com.example.network.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TicketService {

    private static final String TICKETS_ENDPOINT        = "http://localhost:8080/api/v1/tickets";
    private static final String UPDATE_STATUS_ENDPOINT  = "http://localhost:8080/api/v1/tickets/updateStatus";
    private static final String COMMENTS_ENDPOINT       = "http://localhost:8080/api/v1/comments";

    private ApiClient apiClient = new ApiClient();

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String response = apiClient.doGetRequest(TICKETS_ENDPOINT);
        if (response.startsWith("{\"errors\"")) {
            return tickets;
        }
        try {
            JSONArray arr = new JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Ticket ticket = new Ticket();
                ticket.setId(obj.optString("id", "N/A"));
                ticket.setTitle(obj.optString("title", "N/A"));
                ticket.setPriority(obj.optString("priority", "N/A"));
                ticket.setCategory(obj.optString("category", "N/A"));
                ticket.setStatus(obj.optString("status", "N/A"));
                ticket.setAssignedToId(obj.optString("assignedTo_id", "N/A"));
                tickets.add(ticket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tickets;
    }

    public boolean updateTicketStatus(String ticketId, String newStatus) {
        String json = "{\"id\":\"" + ticketId + "\", \"status\":\"" + newStatus + "\"}";
        String response = apiClient.doPostRequest(UPDATE_STATUS_ENDPOINT, json);
        return !response.startsWith("{\"errors\"");
    }

    // Updated method to include user_id as required by CommentReq
    public boolean addComment(String ticketId, String userId, String commentText) {
        String json = "{\"ticket_id\":\"" + ticketId + "\", \"user_id\":\"" + userId + "\", \"message\":\"" + commentText + "\"}";
        String response = apiClient.doPostRequest(COMMENTS_ENDPOINT, json);
        System.out.println(response);
        return !response.startsWith("{\"errors\"");
    }
}
