import { DevicesOtherOutlined } from '@mui/icons-material';
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { FunctionComponent, useEffect, useMemo, useState } from 'react';

import type { EndpointHelper } from '../../../../actions/assets/asset-helper';
import { fetchEndpoints, searchEndpoints } from '../../../../actions/assets/endpoint-actions';
import { buildFilter } from '../../../../components/common/queryable/filter/FilterUtils';
import PaginationComponentV2 from '../../../../components/common/queryable/pagination/PaginationComponentV2';
import { buildSearchPagination } from '../../../../components/common/queryable/QueryableUtils';
import { useQueryable } from '../../../../components/common/queryable/useQueryableWithLocalStorage';
import SelectList, { SelectListElements } from '../../../../components/common/SelectList';
import Transition from '../../../../components/common/Transition';
import { useFormatter } from '../../../../components/i18n';
import ItemTags from '../../../../components/ItemTags';
import PlatformIcon from '../../../../components/PlatformIcon';
import { useHelper } from '../../../../store';
import type { FilterGroup } from '../../../../utils/api-types';
import { useAppDispatch } from '../../../../utils/hooks';
import useDataLoader from '../../../../utils/hooks/useDataLoader';
import type { EndpointStore } from './Endpoint';

interface Props {
  initialState: string[];
  open: boolean;
  onClose: () => void;
  onSubmit: (endpointIds: string[]) => void;
  title: string;
  platforms?: string[];
  payloadType?: string;
  payloadArch?: string;
}

const EndpointsDialogAdding: FunctionComponent<Props> = ({
  initialState = [],
  open,
  onClose,
  onSubmit,
  title,
  platforms,
  payloadType,
  payloadArch,
}) => {
  // Standard hooks
  const dispatch = useAppDispatch();
  const { t } = useFormatter();

  // Fetching data
  const { endpointsMap } = useHelper((helper: EndpointHelper) => ({
    endpointsMap: helper.getEndpointsMap(),
  }));
  useDataLoader(() => {
    dispatch(fetchEndpoints());
  });

  const [endpointValues, setEndpointValues] = useState<EndpointStore[]>(initialState.map(id => endpointsMap[id]));
  useEffect(() => {
    setEndpointValues(initialState.map(id => endpointsMap[id]));
  }, [open, initialState]);

  const addEndpoint = (endpointId: string) => {
    setEndpointValues([...endpointValues, endpointsMap[endpointId]]);
  };
  const removeEndpoint = (endpointId: string) => {
    setEndpointValues(endpointValues.filter(v => v.asset_id !== endpointId));
  };

  // Dialog
  const handleClose = () => {
    setEndpointValues([]);
    onClose();
  };

  const handleSubmit = () => {
    onSubmit(endpointValues.map(v => v.asset_id));
    handleClose();
  };

  // Headers
  const elements: SelectListElements<EndpointStore> = useMemo(() => ({
    icon: {
      value: () => <DevicesOtherOutlined color="primary" />,
    },
    headers: [
      {
        field: 'asset_name',
        value: (endpoint: EndpointStore) => endpoint.asset_name,
        width: 50,
      },
      {
        field: 'endpoint_platform',
        value: (endpoint: EndpointStore) => (
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <PlatformIcon platform={endpoint.endpoint_platform} width={20} marginRight={10} />
            {endpoint.endpoint_platform}
          </div>
        ),
        width: 20,
      },
      {
        field: 'endpoint_arch',
        value: (endpoint: EndpointStore) => endpoint.endpoint_arch,
        width: 20,
      },
      {
        field: 'asset_tags',
        value: (endpoint: EndpointStore) => <ItemTags variant="reduced-view" tags={endpoint.asset_tags} />,
        width: 30,
      },
    ],
  }), []);

  // Pagination
  const [endpoints, setEndpoints] = useState<EndpointStore[]>([]);

  const availableFilterNames = [
    'asset_tags',
    'endpoint_platform',
    'endpoint_arch',
  ];
  const quickFilter: FilterGroup = {
    mode: 'and',
    filters: [
      buildFilter('endpoint_platform', platforms ?? [], 'contains'),
    ],
  };
  if (quickFilter.filters && payloadType === 'Executable' && payloadArch) {
    quickFilter.filters?.push(buildFilter('endpoint_arch', [payloadArch], 'contains'));
  }
  const { queryableHelpers, searchPaginationInput } = useQueryable(buildSearchPagination({
    filterGroup: quickFilter,
  }));

  const paginationComponent = (
    <PaginationComponentV2
      fetch={searchEndpoints}
      searchPaginationInput={searchPaginationInput}
      setContent={setEndpoints}
      entityPrefix="endpoint"
      availableFilterNames={availableFilterNames}
      queryableHelpers={queryableHelpers}
    />
  );

  return (
    <Dialog
      open={open}
      TransitionComponent={Transition}
      onClose={handleClose}
      fullWidth
      maxWidth="lg"
      PaperProps={{
        elevation: 1,
        sx: {
          minHeight: 580,
          maxHeight: 580,
        },
      }}
    >
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <Box sx={{ marginTop: 2 }}>
          <SelectList
            values={endpoints}
            selectedValues={endpointValues}
            elements={elements}
            prefix="asset"
            onSelect={addEndpoint}
            onDelete={removeEndpoint}
            paginationComponent={paginationComponent}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>{t('Cancel')}</Button>
        <Button color="secondary" onClick={handleSubmit}>
          {t('Add')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default EndpointsDialogAdding;
