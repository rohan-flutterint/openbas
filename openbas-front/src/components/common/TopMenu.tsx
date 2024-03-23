import { makeStyles } from '@mui/styles';
import React, { FunctionComponent } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Button } from '@mui/material';
import { useFormatter } from '../i18n';
import type { Theme } from '../Theme';

const useStyles = makeStyles<Theme>((theme) => ({
  button: {
    marginRight: theme.spacing(2),
    padding: '0 5px 0 5px',
    minHeight: 20,
    minWidth: 20,
    textTransform: 'none',
  },
  bar: {
    width: '100%',
  },
}));

export interface MenuEntry {
  path: string;
  match?: string;
  label: string;
}

const TopMenu: FunctionComponent<{ entries: MenuEntry[], contextual?: boolean }> = ({
  entries,
  contextual,
}) => {
  const location = useLocation();
  const classes = useStyles();
  const { t } = useFormatter();

  const buttons = () => (
    entries.map((entry, idx) => {
      let isCurrentTab = false;
      if (entry.match) {
        isCurrentTab = location.pathname.includes(entry.match);
      } else {
        isCurrentTab = location.pathname === entry.path;
      }
      return (
        <Button
          key={idx}
          component={Link}
          to={entry.path}
          size="small"
          variant={isCurrentTab ? 'contained' : 'text'}
          color={isCurrentTab ? 'secondary' : 'primary'}
          classes={{ root: classes.button }}
        >
          {t(entry.label)}
        </Button>
      );
    })
  );

  if (contextual) {
    return <>{buttons()}</>;
  }
  return (
    <div className={classes.bar}>
      {buttons()}
    </div>
  );
};

export default TopMenu;