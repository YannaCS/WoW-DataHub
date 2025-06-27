package game.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import game.etl.ETLProcessor;
import game.etl.ETLProcessor.ETLResult;

@WebServlet("/etl")
public class ETLServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Store running ETL jobs
    private static final Map<String, CompletableFuture<ETLResult>> runningJobs = new ConcurrentHashMap<>();
    private static final Map<String, ETLResult> completedJobs = new ConcurrentHashMap<>();
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            // Show ETL management page
            request.getRequestDispatcher("etl-management.jsp").forward(request, response);
            return;
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            switch (action) {
                case "status":
                    handleStatusRequest(request, response);
                    break;
                case "jobs":
                    handleJobsRequest(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Error processing ETL request: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            switch (action) {
                case "start-realm-sync":
                    handleStartRealmSync(request, response);
                    break;
                case "start-character-sync":
                    handleStartCharacterSync(request, response);
                    break;
                case "stop-job":
                    handleStopJob(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Error processing ETL request: " + e.getMessage());
        }
    }
    
    private void handleStatusRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        String jobId = request.getParameter("jobId");
        
        if (jobId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID required");
            return;
        }
        
        ETLResult result = null;
        
        // Check if job is running
        CompletableFuture<ETLResult> runningJob = runningJobs.get(jobId);
        if (runningJob != null) {
            if (runningJob.isDone()) {
                try {
                    result = runningJob.get();
                    completedJobs.put(jobId, result);
                    runningJobs.remove(jobId);
                } catch (Exception e) {
                    result = new ETLResult();
                    result.setJobId(jobId);
                    result.setStatus("FAILED");
                    result.setErrorMessage(e.getMessage());
                }
            } else {
                result = new ETLResult();
                result.setJobId(jobId);
                result.setStatus("RUNNING");
            }
        } else {
            // Check completed jobs
            result = completedJobs.get(jobId);
        }
        
        if (result == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
            return;
        }
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
    
    private void handleJobsRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        ETLJobsResponse jobsResponse = new ETLJobsResponse();
        
        // Add running jobs
        runningJobs.forEach((jobId, future) -> {
            ETLJobInfo info = new ETLJobInfo();
            info.setJobId(jobId);
            info.setStatus("RUNNING");
            jobsResponse.addRunningJob(info);
        });
        
        // Add completed jobs
        completedJobs.forEach((jobId, result) -> {
            ETLJobInfo info = new ETLJobInfo();
            info.setJobId(result.getJobId());
            info.setJobType(result.getJobType());
            info.setStatus(result.getStatus());
            info.setRecordsProcessed(result.getRecordsProcessed());
            info.setDurationMillis(result.getDurationMillis());
            jobsResponse.addCompletedJob(info);
        });
        
        response.getWriter().write(objectMapper.writeValueAsString(jobsResponse));
    }
    
    private void handleStartRealmSync(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        // Get WoW API credentials
        String clientId = getServletContext().getInitParameter("wow.api.client.id");
        String clientSecret = getServletContext().getInitParameter("wow.api.client.secret");
        
        if (clientId == null || clientSecret == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "WoW API credentials not configured");
            return;
        }
        
        String jobId = "REALM_SYNC_" + System.currentTimeMillis();
        ETLProcessor processor = new ETLProcessor(clientId, clientSecret, 50, 50);
        
        // Start ETL job asynchronously with job ID
        CompletableFuture<ETLResult> future = CompletableFuture.supplyAsync(() -> {
            return processor.processRealmData(jobId);  // Pass job ID here
        });
        
        runningJobs.put(jobId, future);
        
        // Return job ID immediately
        ETLJobStartResponse startResponse = new ETLJobStartResponse();
        startResponse.setJobId(jobId);
        startResponse.setStatus("STARTED");
        startResponse.setMessage("Realm synchronization job started (max 50 records)");
        
        response.getWriter().write(objectMapper.writeValueAsString(startResponse));
    }

    private void handleStartCharacterSync(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        // Get credentials
        String clientId = getServletContext().getInitParameter("wow.api.client.id");
        String clientSecret = getServletContext().getInitParameter("wow.api.client.secret");
        
        if (clientId == null || clientSecret == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "WoW API credentials not configured");
            return;
        }
        
        String jobId = "CHARACTER_SYNC_" + System.currentTimeMillis();
        ETLProcessor processor = new ETLProcessor(clientId, clientSecret, 50, 50);
        
        // Start character sync job asynchronously with job ID
        CompletableFuture<ETLResult> future = CompletableFuture.supplyAsync(() -> {
            return processor.processCharacterData(jobId);  // Pass job ID here
        });
        
        runningJobs.put(jobId, future);
        
        ETLJobStartResponse startResponse = new ETLJobStartResponse();
        startResponse.setJobId(jobId);
        startResponse.setStatus("STARTED");
        startResponse.setMessage("Character synchronization job started (max 50 records)");
        
        response.getWriter().write(objectMapper.writeValueAsString(startResponse));
    }
    
    private void handleStopJob(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        String jobId = request.getParameter("jobId");
        if (jobId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID required");
            return;
        }
        
        CompletableFuture<ETLResult> future = runningJobs.get(jobId);
        if (future != null) {
            future.cancel(true);
            runningJobs.remove(jobId);
            
            ETLResult result = new ETLResult();
            result.setJobId(jobId);
            result.setStatus("CANCELLED");
            completedJobs.put(jobId, result);
        }
        
        ETLJobStartResponse stopResponse = new ETLJobStartResponse();
        stopResponse.setJobId(jobId);
        stopResponse.setStatus("STOPPED");
        stopResponse.setMessage("Job stopped successfully");
        
        response.getWriter().write(objectMapper.writeValueAsString(stopResponse));
    }
    
    // Response classes
    public static class ETLJobStartResponse {
        private String jobId;
        private String status;
        private String message;
        
        // Getters and setters
        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class ETLJobsResponse {
        private java.util.List<ETLJobInfo> runningJobs = new java.util.ArrayList<>();
        private java.util.List<ETLJobInfo> completedJobs = new java.util.ArrayList<>();
        
        // Getters and setters
        public java.util.List<ETLJobInfo> getRunningJobs() { return runningJobs; }
        public void setRunningJobs(java.util.List<ETLJobInfo> runningJobs) { this.runningJobs = runningJobs; }
        public void addRunningJob(ETLJobInfo job) { this.runningJobs.add(job); }
        
        public java.util.List<ETLJobInfo> getCompletedJobs() { return completedJobs; }
        public void setCompletedJobs(java.util.List<ETLJobInfo> completedJobs) { this.completedJobs = completedJobs; }
        public void addCompletedJob(ETLJobInfo job) { this.completedJobs.add(job); }
    }
    
    public static class ETLJobInfo {
        private String jobId;
        private String jobType;
        private String status;
        private int recordsProcessed;
        private long durationMillis;
        
        // Getters and setters
        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        
        public String getJobType() { return jobType; }
        public void setJobType(String jobType) { this.jobType = jobType; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public int getRecordsProcessed() { return recordsProcessed; }
        public void setRecordsProcessed(int recordsProcessed) { this.recordsProcessed = recordsProcessed; }
        
        public long getDurationMillis() { return durationMillis; }
        public void setDurationMillis(long durationMillis) { this.durationMillis = durationMillis; }
    }
}