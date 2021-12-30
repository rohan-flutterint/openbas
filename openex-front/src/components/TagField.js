import React, { Component } from 'react';
import * as R from 'ramda';
import { LabelOutlined } from '@mui/icons-material';
import Box from '@mui/material/Box';
import { withStyles } from '@mui/styles';
import { connect } from 'react-redux';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import TagForm from '../private/components/settings/tag/TagForm';
import { fetchTags, addTag } from '../actions/Tag';
import { Autocomplete } from './Autocomplete';
import inject18n from './i18n';
import { storeBrowser } from '../actions/Schema';

const styles = () => ({
  icon: {
    paddingTop: 4,
    display: 'inline-block',
  },
  text: {
    display: 'inline-block',
    flexGrow: 1,
    marginLeft: 10,
  },
  autoCompleteIndicator: {
    display: 'none',
  },
});

class TagField extends Component {
  constructor(props) {
    super(props);
    this.state = { tagCreation: false, tagInput: '' };
  }

  componentDidMount() {
    this.props.fetchTags();
  }

  handleOpenTagCreation() {
    this.setState({ tagCreation: true });
  }

  handleCloseTagCreation() {
    this.setState({ tagCreation: false });
  }

  onSubmit(data) {
    this.props
      .addTag(data)
      .then((result) => (result.result ? this.handleCloseTagCreation() : result));
  }

  render() {
    const {
      t, name, tags, classes,
    } = this.props;
    const tagsOptions = R.map(
      (n) => ({
        id: n.tag_id,
        label: n.tag_name,
        color: n.tag_color,
      }),
      tags,
    );
    return (
      <div>
        <Autocomplete
          variant="standard"
          size="small"
          name={name}
          fullWidth={true}
          multiple={true}
          label={t('Tags')}
          options={tagsOptions}
          style={{ marginTop: 20 }}
          openCreate={this.handleOpenTagCreation.bind(this)}
          renderOption={(props, option) => (
            <Box component="li" {...props}>
              <div className={classes.icon} style={{ color: option.color }}>
                <LabelOutlined />
              </div>
              <div className={classes.text}>{option.label}</div>
            </Box>
          )}
          classes={{ clearIndicator: classes.autoCompleteIndicator }}
        />
        <Dialog
          open={this.state.tagCreation}
          onClose={this.handleCloseTagCreation.bind(this)}
        >
          <DialogTitle>{t('Create a new tag')}</DialogTitle>
          <DialogContent>
            <TagForm onSubmit={this.onSubmit.bind(this)} />
          </DialogContent>
          <DialogActions>
            <Button
              variant="contained"
              color="secondary"
              onClick={this.handleCloseTagCreation.bind(this)}
            >
              {t('Cancel')}
            </Button>
            <Button
              variant="contained"
              color="primary"
              type="submit"
              form="tagForm"
            >
              {t('Create')}
            </Button>
          </DialogActions>
        </Dialog>
      </div>
    );
  }
}

const select = (state) => {
  const browser = storeBrowser(state);
  return {
    tags: browser.getTags(),
  };
};

export default R.compose(
  connect(select, { fetchTags, addTag }),
  inject18n,
  withStyles(styles),
)(TagField);