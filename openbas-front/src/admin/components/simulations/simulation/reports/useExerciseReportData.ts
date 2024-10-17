import { Dispatch, SetStateAction, useEffect, useState } from 'react';
import { useHelper } from '../../../../../store';
import type { ReportsHelper } from '../../../../../actions/reports/report-helper';
import useDataLoader from '../../../../../utils/hooks/useDataLoader';
import { fetchReport } from '../../../../../actions/reports/report-actions';
import type { InjectHelper } from '../../../../../actions/injects/inject-helper';
import type { ExercisesHelper } from '../../../../../actions/exercises/exercise-helper';
import type { TeamsHelper } from '../../../../../actions/teams/team-helper';
import ReportInformationType from './ReportInformationType';
import { fetchExercise, fetchExerciseTeams } from '../../../../../actions/Exercise';
import {
  exerciseInjectsResultDTO,
  fetchExerciseExpectationResult,
  fetchLessonsAnswers,
  fetchLessonsCategories,
  fetchLessonsQuestions,
} from '../../../../../actions/exercises/exercise-action';
import { useAppDispatch } from '../../../../../utils/hooks';
import type {
  Exercise,
  ExpectationResultsByType,
  InjectResultDTO,
  LessonsAnswer,
  LessonsCategory,
  LessonsQuestion,
  Report,
  ReportInformation,
  Team,
} from '../../../../../utils/api-types';

export interface ExerciseReportData {
  injects: InjectResultDTO[],
  exerciseExpectationResults: ExpectationResultsByType[],
  exercise: Exercise,
  lessonsCategories: LessonsCategory[],
  lessonsQuestions: LessonsQuestion[],
  lessonsAnswers: LessonsAnswer[],
  teams: Team[],
  teamsMap: Record<string, Team>,
}

interface ReturnType {
  loading: boolean,
  report: Report,
  displayModule: (moduleType: ReportInformationType)=>boolean,
  setReloadReportDataCount: Dispatch<SetStateAction<number>>
  reportData: ExerciseReportData
}

const useExerciseReportData = (reportId: Report['report_id'], exerciseId: Exercise['exercise_id']): ReturnType => {
  const dispatch = useAppDispatch();
  const [loading, setLoading] = useState(true);
  const [reloadReportDataCount, setReloadReportDataCount] = useState(0);
  const [exerciseExpectationResults, setResults] = useState<ExpectationResultsByType[]>([]);
  const [injects, setInjects] = useState<InjectResultDTO[]>([]);

  const {
    report,
    exercise,
    lessonsCategories,
    lessonsQuestions,
    lessonsAnswers,
    teams,
    teamsMap,
  } = useHelper((helper: InjectHelper & ReportsHelper & ExercisesHelper & TeamsHelper) => {
    return {
      report: helper.getReport(reportId),
      exercise: helper.getExercise(exerciseId),
      lessonsCategories: helper.getExerciseLessonsCategories(exerciseId),
      lessonsQuestions: helper.getExerciseLessonsQuestions(exerciseId),
      lessonsAnswers: helper.getExerciseLessonsAnswers(exerciseId),
      teamsMap: helper.getTeamsMap(),
      teams: helper.getExerciseTeams(exerciseId),
    };
  });

  const displayModule = (moduleType: ReportInformationType): boolean => {
    return report?.report_informations.find((info: ReportInformation) => info.report_informations_type === moduleType)?.report_informations_display;
  };

  useDataLoader(() => {
    dispatch(fetchReport(reportId)).then(() => {
      setReloadReportDataCount((prev) => prev + 1);
    });
  });

  useEffect(() => {
    if (reloadReportDataCount > 0) {
      setLoading(true);
      const fetchPromises = [];
      fetchPromises.push(dispatch(fetchExercise(exerciseId)));
      fetchPromises.push(dispatch(fetchExerciseTeams(exerciseId)));
      if (displayModule(ReportInformationType.PLAYER_SURVEYS)) {
        fetchPromises.push(
          dispatch(fetchLessonsQuestions(exerciseId)),
          dispatch(fetchLessonsAnswers(exerciseId)),
          dispatch(fetchLessonsCategories(exerciseId)),
        );
      }
      if (displayModule(ReportInformationType.SCORE_DETAILS)) {
        fetchPromises.push(fetchExerciseExpectationResult(exerciseId).then((result) => setResults(result.data)));
      }

      if (displayModule(ReportInformationType.INJECT_RESULT)) {
        fetchPromises.push(exerciseInjectsResultDTO(exerciseId).then((result) => setInjects(result.data)));
      }
      Promise.all(fetchPromises).then(() => {
        setLoading(false);
      });
    }
  }, [reloadReportDataCount]);

  return {
    loading,
    report,
    displayModule,
    setReloadReportDataCount,
    reportData: {
      injects,
      exerciseExpectationResults,
      exercise,
      lessonsCategories,
      lessonsQuestions,
      lessonsAnswers,
      teams,
      teamsMap,
    },
  };
};

export default useExerciseReportData;
