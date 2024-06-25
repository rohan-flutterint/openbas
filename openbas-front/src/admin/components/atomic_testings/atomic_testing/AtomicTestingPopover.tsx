import React, { FunctionComponent, useContext, useState } from 'react';
import * as R from 'ramda';
import { useNavigate } from 'react-router-dom';
import type { AtomicTestingInput, Inject, InjectResultDTO } from '../../../../utils/api-types';
import { useFormatter } from '../../../../components/i18n';
import { useAppDispatch } from '../../../../utils/hooks';
import ButtonPopover, { ButtonPopoverEntry } from '../../../../components/common/ButtonPopover';
import DialogDelete from '../../../../components/common/DialogDelete';
import { createAtomicTesting, deleteAtomicTesting, updateAtomicTesting } from '../../../../actions/atomic_testings/atomic-testing-actions';
import { useHelper } from '../../../../store';
import useDataLoader from '../../../../utils/hooks/useDataLoader';
import UpdateInject from '../../common/injects/UpdateInject';
import type { TeamsHelper } from '../../../../actions/teams/team-helper';
import { fetchTeams } from '../../../../actions/teams/team-actions';
import type { TeamStore } from '../../../../actions/teams/Team';
import { isNotEmptyField } from '../../../../utils/utils';
import { InjectResultDtoContext, InjectResultDtoContextType } from '../InjectResultDtoContext';
import DialogDuplicate from '../../../../components/common/DialogDuplicate';

interface Props {
  atomic: InjectResultDTO;
  openEdit?: boolean;
  openDelete?: boolean;
  openDuplicate?: boolean;
  setOpenEdit?: React.Dispatch<React.SetStateAction<boolean>>;
  setOpenDelete?: React.Dispatch<React.SetStateAction<boolean>>;
  setOpenDuplicate?: React.Dispatch<React.SetStateAction<boolean>>;
  entries: ButtonPopoverEntry[];
}

const AtomicTestingPopover: FunctionComponent<Props> = ({
  atomic,
  entries,
  openEdit,
  openDelete,
  openDuplicate,
  setOpenEdit,
  setOpenDelete,
  setOpenDuplicate,
}) => {
  // Standard hooks
  const { t } = useFormatter();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [edition, setEdition] = useState(false);
  const [deletion, setDeletion] = useState(false);
  const [duplicate, setDuplicate] = useState(false);

  // Fetching data
  const { updateInjectResultDto } = useContext<InjectResultDtoContextType>(InjectResultDtoContext);
  const { teams } = useHelper((helper: TeamsHelper) => ({
    teams: helper.getTeams(),
  }));
  useDataLoader(() => {
    dispatch(fetchTeams());
  });

  const onUpdateAtomicTesting = async (data: Inject) => {
    const toUpdate = R.pipe(
      R.pick([
        'inject_tags',
        'inject_title',
        'inject_type',
        'inject_injector_contract',
        'inject_description',
        'inject_content',
        'inject_all_teams',
        'inject_documents',
        'inject_assets',
        'inject_asset_groups',
        'inject_teams',
        'inject_tags',
      ]),
    )(data);
    updateAtomicTesting(atomic.inject_id, toUpdate).then((result: { data: InjectResultDTO }) => {
      updateInjectResultDto(result.data);
    });
  };

  const submitDelete = () => {
    deleteAtomicTesting(atomic.inject_id).then(() => {
      if (setDeletion) {
        setDeletion(false);
      }
      navigate('/admin/atomic_testings');
    });
  };

  const submitDuplicate = async (data: AtomicTestingInput) => {
    const toDuplicate = R.pipe(
      R.pick([
        'inject_id',
      ]),
    )(data);
    await createAtomicTesting(toDuplicate).then((result: { data: InjectResultDTO }) => {
      navigate(`/admin/atomic_testings/${result.data.inject_id}`);
    });
  };

  const submitDuplicateHandler = () => {
    const data: AtomicTestingInput = { inject_id: atomic.inject_id }; // Adaptez selon vos besoins
    submitDuplicate(data);
  };

  return (
    <>
      <ButtonPopover entries={entries} />
      <UpdateInject
        open={isNotEmptyField(openEdit) ? openEdit : edition}
        handleClose={() => (setOpenEdit ? setOpenEdit(false) : setEdition(false))}
        onUpdateInject={onUpdateAtomicTesting}
        injectId={atomic.inject_id}
        isAtomic
        teamsFromExerciseOrScenario={teams?.filter((team: TeamStore) => !team.team_contextual) ?? []}
      />
      <DialogDelete
        open={isNotEmptyField(openDelete) ? openDelete : deletion}
        handleClose={() => (setOpenDelete ? setOpenDelete(false) : setDeletion(false))}
        handleSubmit={submitDelete}
        text={t('Do you want to delete this atomic testing ?')}
      />
      <DialogDuplicate
        open={isNotEmptyField(openDuplicate) ? openDuplicate : duplicate}
        handleClose={() => (setOpenDuplicate ? setOpenDuplicate(false) : setDuplicate(false))}
        handleSubmit={submitDuplicateHandler}
        text={t('Do you want to duplicate this atomic testing?')}
      />
    </>
  );
};

export default AtomicTestingPopover;
