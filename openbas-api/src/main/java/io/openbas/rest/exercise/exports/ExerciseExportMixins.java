package io.openbas.rest.exercise.exports;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

public class ExerciseExportMixins {

  @JsonIgnoreProperties(value = {"exercise_users", "exercise_organizations"})
  public static class ExerciseFileExport {}

  @JsonIncludeProperties(
      value = {
        "exercise_id",
        "exercise_name",
        "exercise_description",
        "exercise_subtitle",
        "exercise_image",
        "exercise_message_header",
        "exercise_message_footer",
        "exercise_mail_from",
        "exercise_tags",
        "exercise_documents",
      })
  public static class Exercise {}

  @JsonIncludeProperties(
      value = {
        "document_id",
        "document_name",
        "document_target",
        "document_description",
        "document_tags",
      })
  public static class Document {}

  @JsonIncludeProperties(
      value = {
        "organization_id",
        "organization_name",
        "organization_description",
        "organization_tags",
      })
  public static class Organization {}

  @JsonIncludeProperties(
      value = {
        "team_id",
        "team_name",
        "team_description",
        "team_tags",
        "team_organization",
        "team_users",
      })
  public static class Team {}

  @JsonIncludeProperties(
      value = {
        "team_id",
        "team_name",
        "team_description",
        "team_tags",
      })
  public static class EmptyTeam {}

  @JsonIncludeProperties(
      value = {
        "inject_id",
        "inject_title",
        "inject_description",
        "inject_country",
        "inject_city",
        "inject_injector_contract",
        "inject_all_teams",
        "inject_depends_on",
        "inject_depends_duration",
        "inject_tags",
        "inject_documents",
        "inject_teams",
        "inject_content",
      })
  public static class Inject {}

  @JsonIncludeProperties(
      value = {
        "user_id",
        "user_firstname",
        "user_lastname",
        "user_lang",
        "user_email",
        "user_phone",
        "user_pgp_key",
        "user_organization",
        "user_country",
        "user_city",
        "user_tags",
      })
  public static class User {}

  @JsonIncludeProperties(
      value = {
        "objective_id",
        "objective_title",
        "objective_description",
        "objective_priority",
      })
  public static class Objective {}

  @JsonIncludeProperties(
      value = {
        "poll_id",
        "poll_question",
      })
  public static class Poll {}

  @JsonIncludeProperties(
      value = {
        "tag_id",
        "tag_name",
        "tag_color",
      })
  public static class Tag {}

  @JsonIncludeProperties(
      value = {
        "channel_id",
        "channel_type",
        "channel_name",
        "channel_description",
        "channel_mode",
        "channel_primary_color_dark",
        "channel_primary_color_light",
        "channel_secondary_color_dark",
        "channel_secondary_color_light",
        "channel_logo_dark",
        "channel_logo_light",
      })
  public static class Channel {}

  @JsonIncludeProperties(
      value = {
        "article_id",
        "article_name",
        "article_content",
        "article_author",
        "article_shares",
        "article_likes",
        "article_comments",
        "article_channel",
        "article_documents",
        "article_exercise",
      })
  public static class Article {}

  @JsonIncludeProperties(
      value = {
        "challenge_id",
        "challenge_name",
        "challenge_category",
        "challenge_content",
        "challenge_score",
        "challenge_max_attempts",
        "challenge_flags",
        "challenge_tags",
        "challenge_documents",
      })
  public static class Challenge {}

  @JsonIncludeProperties(
      value = {
        "lessonscategory_id",
        "lessons_category_name",
        "lessons_category_description",
        "lessons_category_order",
        "lessons_category_questions",
        "lessons_category_teams",
      })
  public static class LessonsCategory {}

  @JsonIncludeProperties(
      value = {
        "lessonsquestion_id",
        "lessons_question_category",
        "lessons_question_content",
        "lessons_question_explanation",
        "lessons_question_order",
      })
  public static class LessonsQuestion {}
}
