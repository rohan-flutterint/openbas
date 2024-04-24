import { useTheme } from '@mui/styles';
import React, { FunctionComponent } from 'react';
import Chart from 'react-apexcharts';
import * as R from 'ramda';
import Empty from '../../../../components/Empty';
import type { ExerciseStore } from '../../../../actions/exercises/Exercise';
import { useFormatter } from '../../../../components/i18n';
import { useAppDispatch } from '../../../../utils/hooks';
import { useHelper } from '../../../../store';
import useDataLoader from '../../../../utils/ServerSideEvent';
import { horizontalBarsChartOptions } from '../../../../utils/Charts';
import type { Theme } from '../../../../components/Theme';
import type { InjectHelper } from '../../../../actions/injects/inject-helper';
import { fetchExerciseInjects } from '../../../../actions/Inject';
import { fetchExerciseChallenges } from '../../../../actions/Challenge';
import type { ChallengesHelper } from '../../../../actions/helper';
import type { InjectExpectationStore, InjectStore } from '../../../../actions/injects/Inject';
import type { InjectorContractHelper } from '../../../../actions/injector_contracts/injector-contract-helper';

interface Props {
  exerciseId: ExerciseStore['exercise_id'];
}

const MailDistributionScoreOverTime: FunctionComponent<Props> = ({
  exerciseId,
}) => {
  // Standard hooks
  const { t, tPick } = useFormatter();
  const dispatch = useAppDispatch();
  const theme: Theme = useTheme();

  // Fetching data
  const { injects, challengesMap, injectorContractsMap } = useHelper((helper: InjectHelper & InjectorContractHelper & ChallengesHelper) => ({
    injects: helper.getExerciseInjects(exerciseId),
    challengesMap: helper.getChallengesMap(),
    injectorContractsMap: helper.getInjectorContractsMapByType(),
  }));
  useDataLoader(() => {
    dispatch(fetchExerciseInjects(exerciseId));
    dispatch(fetchExerciseChallenges(exerciseId));
  });

  const injectorContractsWithScore = R.pipe(
    R.filter(
      (n: InjectStore) => n.inject_type === 'openbas_challenge'
        || n.inject_content?.expectationScore,
    ),
    R.map((n: InjectStore) => {
      if (n.inject_type !== 'openbas_challenge') {
        return R.assoc('inject_score', n.inject_content.expectationScore, n);
      }
      return R.assoc(
        'inject_score',
        R.sum(
          (n.inject_content?.challenges || []).map(
            (c) => challengesMap[c]?.challenge_score || 0,
          ),
        ),
        n,
      );
    }),
    R.groupBy(R.prop('inject_type')),
    R.toPairs,
    R.map((n: [string, InjectExpectationStore[]]) => ({
      inject_type: n[0],
      score: R.sum(n[1].map((i: InjectStore & { inject_score: number }) => i.inject_score)),
      number: R.sum(n[1].map((i: InjectStore) => i.inject_expectations?.length)),
    })),
  )(injects);
  const sortedInjectorContractsWithScoreByScore = R.pipe(
    R.sortWith([R.descend(R.prop('score'))]),
    R.take(10),
  )(injectorContractsWithScore);
  const expectedScoreByInjectorContractData = [
    {
      name: t('Total expected score'),
      data: sortedInjectorContractsWithScoreByScore.map((a: InjectStore & { score: number }) => ({
        x: tPick(injectorContractsMap && injectorContractsMap[a.inject_type]?.label),
        y: a.score,
        fillColor:
          injectorContractsMap && injectorContractsMap[a.inject_type]?.config?.color,
      })),
    },
  ];

  return (
    <>
      {injectorContractsWithScore.length > 0 ? (
        <Chart
          // @ts-expect-error: Need to migrate Chart.js file
          options={horizontalBarsChartOptions(theme)}
          series={expectedScoreByInjectorContractData}
          type="bar"
          width="100%"
          height={50 + injectorContractsWithScore.length * 50}
        />
      ) : (
        <Empty
          message={t(
            'No data to display or the simulation has not started yet',
          )}
        />
      )}
    </>
  );
};

export default MailDistributionScoreOverTime;