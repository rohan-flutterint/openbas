package io.openbas.rest.report;

import static io.openbas.database.model.User.ROLE_USER;

import io.openbas.database.model.*;
import io.openbas.rest.exercise.ExerciseService;
import io.openbas.rest.helper.RestBehavior;
import io.openbas.rest.report.form.ReportInjectCommentInput;
import io.openbas.rest.report.form.ReportInput;
import io.openbas.service.InjectService;
import io.openbas.service.ReportService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Secured(ROLE_USER)
public class ReportApi extends RestBehavior {

  private final ExerciseService exerciseService;
  private final ReportService reportService;
  private final InjectService injectService;

  @GetMapping("/api/reports/{reportId}")
  @PreAuthorize("isObserver()")
  public Report report(@PathVariable String reportId) {
    return this.reportService.report(UUID.fromString(reportId));
  }

  @GetMapping("/api/exercises/{exerciseId}/reports")
  @PreAuthorize("isExerciseObserver(#exerciseId)")
  public Iterable<Report> exerciseReports(@PathVariable String exerciseId) {
    return this.reportService.reportsFromExercise(exerciseId);
  }

  @PostMapping("/api/exercises/{exerciseId}/reports")
  @PreAuthorize("isExercisePlanner(#exerciseId)")
  @Transactional(rollbackOn = Exception.class)
  public Report createExerciseReport(
      @PathVariable String exerciseId, @Valid @RequestBody ReportInput input) {
    Exercise exercise = this.exerciseService.exercise(exerciseId);
    Report report = new Report();
    report.setExercise(exercise);
    return this.reportService.updateReport(report, input);
  }

  @PutMapping("/api/exercises/{exerciseId}/reports/{reportId}/inject-comments")
  @PreAuthorize("isExercisePlanner(#exerciseId)")
  @Transactional(rollbackOn = Exception.class)
  public Iterable<ReportInjectComment> updateReportInjectComment(
      @PathVariable String exerciseId,
      @PathVariable String reportId,
      @Valid @RequestBody ReportInjectCommentInput input) {
    Report report = this.reportService.report(UUID.fromString(reportId));
    assert exerciseId.equals(report.getExercise().getId());
    Inject inject = this.injectService.inject(input.getInjectId());
    assert exerciseId.equals(inject.getExercise().getId());
    return this.reportService.updateReportInjectComment(report, inject, input);
  }

  @PutMapping("/api/exercises/{exerciseId}/reports/{reportId}")
  @PreAuthorize("isExercisePlanner(#exerciseId)")
  @Transactional(rollbackOn = Exception.class)
  public Report updateExerciseReport(
      @PathVariable String exerciseId,
      @PathVariable String reportId,
      @Valid @RequestBody ReportInput input) {
    Report report = this.reportService.report(UUID.fromString(reportId));
    assert exerciseId.equals(report.getExercise().getId());
    return this.reportService.updateReport(report, input);
  }

  @DeleteMapping("/api/exercises/{exerciseId}/reports/{reportId}")
  @PreAuthorize("isExercisePlanner(#exerciseId)")
  @Transactional(rollbackOn = Exception.class)
  public void deleteExerciseReport(@PathVariable String exerciseId, @PathVariable String reportId) {
    Report report = this.reportService.report(UUID.fromString(reportId));
    assert exerciseId.equals(report.getExercise().getId());
    this.reportService.deleteReport(UUID.fromString(reportId));
  }
}
