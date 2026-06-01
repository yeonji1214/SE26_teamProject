package its.api.dto;

import java.util.ArrayList;
import java.util.List;

public class RecommendationResponse {
    private UserResponse assignee;
    private double score;
    private int matchedIssueCount;
    private List<String> matchedTerms = new ArrayList<>();
    private List<String> evidenceIssueTitles = new ArrayList<>();
    private int currentOpenAssignedIssueCount;
    private String explanation;

    public RecommendationResponse() {
    }

    public RecommendationResponse(
            UserResponse assignee,
            double score,
            int matchedIssueCount,
            List<String> matchedTerms,
            List<String> evidenceIssueTitles,
            int currentOpenAssignedIssueCount,
            String explanation
    ) {
        this.assignee = assignee;
        this.score = score;
        this.matchedIssueCount = matchedIssueCount;
        this.matchedTerms = matchedTerms == null ? new ArrayList<>() : new ArrayList<>(matchedTerms);
        this.evidenceIssueTitles = evidenceIssueTitles == null ? new ArrayList<>() : new ArrayList<>(evidenceIssueTitles);
        this.currentOpenAssignedIssueCount = currentOpenAssignedIssueCount;
        this.explanation = explanation;
    }

    public UserResponse getAssignee() {
        return assignee;
    }

    public double getScore() {
        return score;
    }

    public int getMatchedIssueCount() {
        return matchedIssueCount;
    }

    public List<String> getMatchedTerms() {
        return matchedTerms;
    }

    public List<String> getEvidenceIssueTitles() {
        return evidenceIssueTitles;
    }

    public int getCurrentOpenAssignedIssueCount() {
        return currentOpenAssignedIssueCount;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setAssignee(UserResponse assignee) {
        this.assignee = assignee;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setMatchedIssueCount(int matchedIssueCount) {
        this.matchedIssueCount = matchedIssueCount;
    }

    public void setMatchedTerms(List<String> matchedTerms) {
        this.matchedTerms = matchedTerms == null ? new ArrayList<>() : new ArrayList<>(matchedTerms);
    }

    public void setEvidenceIssueTitles(List<String> evidenceIssueTitles) {
        this.evidenceIssueTitles = evidenceIssueTitles == null ? new ArrayList<>() : new ArrayList<>(evidenceIssueTitles);
    }

    public void setCurrentOpenAssignedIssueCount(int currentOpenAssignedIssueCount) {
        this.currentOpenAssignedIssueCount = currentOpenAssignedIssueCount;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}