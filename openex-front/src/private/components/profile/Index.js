import React, { useEffect } from 'react';
import * as PropTypes from 'prop-types';
import { connect } from 'react-redux';
import * as R from 'ramda';
import Paper from '@mui/material/Paper';
import Button from '@mui/material/Button';
import { withStyles } from '@mui/styles';
import Typography from '@mui/material/Typography';
import { fetchOrganizations } from '../../../actions/Organization';
import {
  updateMeProfile, updateMeInformation, updateMePassword, meTokens,
} from '../../../actions/User';
import UserForm from './UserForm';
import ProfileForm from './ProfileForm';
import PasswordForm from './PasswordForm';
import { storeBrowser } from '../../../actions/Schema';
import inject18n from '../../../components/i18n';

const styles = (theme) => ({
  paper: {
    padding: 20,
    marginBottom: 40,
  },
  appBar: {
    width: '100%',
    zIndex: theme.zIndex.drawer + 1,
  },
  container: {
    padding: 20,
  },
  logo: {
    width: '40px',
    cursor: 'pointer',
  },
  title: {
    fontSize: 25,
    marginLeft: 20,
  },
  toolbar: theme.mixins.toolbar,
  documents: {
    color: '#ffffff',
    position: 'absolute',
    top: 8,
    right: 70,
  },
});

const Index = (props) => {
  useEffect(() => {
    props.fetchOrganizations();
    props.meTokens();
  }, []);

  const {
    classes, user, organizations, t,
  } = props;
  const userTokens = user.getTokens();
  const organization = user.getOrganization();
  const initOrganization = organization
    ? { id: organization?.organization_id, label: organization?.organization_name } : undefined;

  const onUpdateProfile = (data) => {
    const inputValues = R.assoc('user_organization', data.user_organization.id, data);
    props.updateMeProfile(inputValues);
  };

  const onUpdateInformation = (data) => props.updateMeInformation(data);

  const onUpdatePassword = (data) => props.updateMePassword(data.user_plain_password);

  const initPipe = R.pipe(
    R.assoc('user_organization', initOrganization),
    R.pick([
      'user_firstname',
      'user_lastname',
      'user_lang',
      'user_email',
      'user_organization',
      'user_phone',
      'user_phone2',
      'user_pgp_key',
    ]),
  );
  const informationValues = user !== undefined ? initPipe(user) : undefined;
  const userToken = userTokens.length > 0 ? R.head(userTokens) : undefined;
  return (
      <div className={classes.container}>
        <div style={{ width: 800, margin: '0 auto' }}>
          <Paper variant="outlined" className={classes.paper}>
            <Typography variant="h5" style={{ marginBottom: 20 }}>
              {t('Profile')}
            </Typography>
            <UserForm
              organizations={organizations}
              onSubmit={onUpdateProfile}
              initialValues={informationValues}
            />
            <br />
            <Button
              variant="contained"
              color="primary"
              type="submit"
              form="userForm">
              {t('Update')}
            </Button>
          </Paper>
          <Paper variant="outlined" className={classes.paper}>
            <Typography variant="h5" style={{ marginBottom: 20 }}>
              {t('Information')}
            </Typography>
            <ProfileForm
              onSubmit={onUpdateInformation}
              initialValues={informationValues}
            />
            <br />
            <Button
              variant="contained"
              color="primary"
              type="submit"
              form="profileForm"
            >
              {t('Update')}
            </Button>
          </Paper>
          <Paper variant="outlined" className={classes.paper}>
            <Typography variant="h5" style={{ marginBottom: 20 }}>
              {t('Password')}
            </Typography>
            <PasswordForm onSubmit={onUpdatePassword} />
            <br />
            <Button
              variant="contained"
              color="primary"
              type="submit"
              form="passwordForm">
              {t('Update')}
            </Button>
          </Paper>
          <Paper variant="outlined" className={classes.paper}>
            <Typography variant="h5" style={{ marginBottom: 20 }}>
              {t('API access')}
            </Typography>
            <Typography variant="body1">
              {t(
                'The OpenEX API relies on the REST standard, using HTTP verbs. The token must be passed into the HTTP heade',
              )}{' '}
              <strong>X-Authorization-Token</strong>.
              <Typography
                variant="h6"
                gutterBottom={true}
                style={{ marginTop: 20 }}>
                {t('Token key')}
              </Typography>
              <pre>{userToken?.token_value}</pre>
              <Typography variant="h6" gutterBottom={true}>
                {t('Example')}
              </Typography>
              <pre>
                GET /api/exercises
                <br />
                Content-Type: application/json
                <br />
                X-Authorization-Token: {userToken?.token_value}
              </pre>
            </Typography>
            <Button
              variant="contained"
              color="primary"
              component="a"
              href="/swagger-ui/">
              {t('API documentation')}
            </Button>
          </Paper>
        </div>
      </div>
  );
};

Index.propTypes = {
  user: PropTypes.object,
  organizations: PropTypes.object,
  fetchOrganizations: PropTypes.func,
  updateUser: PropTypes.func,
  updateUserPassword: PropTypes.func,
};

const select = (state) => {
  const browser = storeBrowser(state);
  const user = browser.getMe();
  const organizations = browser.getOrganizations();
  return { user, organizations };
};

export default R.compose(
  connect(select, {
    fetchOrganizations,
    meTokens,
    updateMeProfile,
    updateMeInformation,
    updateMePassword,
  }),
  inject18n,
  withStyles(styles),
)(Index);