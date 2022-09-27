import React from 'react';
import { makeStyles } from '@mui/styles';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import { CastForEducationOutlined, HelpOutlined } from '@mui/icons-material';
import Typography from '@mui/material/Typography';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import ListItemSecondaryAction from '@mui/material/ListItemSecondaryAction';
import LinearProgress from '@mui/material/LinearProgress';
import * as R from 'ramda';
import Tooltip from '@mui/material/Tooltip';
import Chip from '@mui/material/Chip';
import { useDispatch } from 'react-redux';
import { useFormatter } from '../../../../components/i18n';
import LessonsCategoryPopover from './categories/LessonsCategoryPopover';
import LessonsQuestionPopover from './categories/questions/LessonsQuestionPopover';
import CreateLessonsQuestion from './categories/questions/CreateLessonsQuestion';
import LessonsCategoryAddAudiences from './categories/LessonsCategoryAddAudiences';
import { truncate } from '../../../../utils/String';
import { updateLessonsCategoryAudiences } from '../../../../actions/Lessons';

const useStyles = makeStyles((theme) => ({
  container: {
    margin: '10px 0 50px 0',
    padding: '0 200px 0 0',
  },
  metric: {
    position: 'relative',
    padding: 20,
    height: 100,
    overflow: 'hidden',
  },
  title: {
    textTransform: 'uppercase',
    fontSize: 12,
    fontWeight: 500,
    color: theme.palette.text.secondary,
  },
  number: {
    fontSize: 30,
    fontWeight: 800,
    float: 'left',
  },
  icon: {
    position: 'absolute',
    top: 25,
    right: 15,
  },
  paper: {
    position: 'relative',
    padding: 0,
    overflow: 'hidden',
    height: '100%',
  },
  paperPadding: {
    position: 'relative',
    padding: '20px 20px 0 20px',
    overflow: 'hidden',
    height: '100%',
  },
  paperChart: {
    position: 'relative',
    padding: '0 20px 0 0',
    overflow: 'hidden',
    height: '100%',
  },
  card: {
    width: '100%',
    height: '100%',
    marginBottom: 30,
    borderRadius: 6,
    padding: 0,
    position: 'relative',
  },
  heading: {
    display: 'flex',
  },
  chip: {
    margin: '0 10px 10px 0',
  },
}));

const LessonsCategories = ({
  exerciseId,
  lessonsCategories,
  lessonsAnswers,
  lessonsQuestions,
  setSelectedQuestion,
  audiencesMap,
  isReport,
}) => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const { t } = useFormatter();
  const sortCategories = R.sortWith([
    R.ascend(R.prop('lessons_category_order')),
  ]);
  const sortQuestions = R.sortWith([
    R.ascend(R.prop('lessons_question_order')),
  ]);
  const sortedCategories = sortCategories(lessonsCategories);
  const handleUpdateAudiences = (lessonsCategoryId, audiencesIds) => {
    const data = { lessons_category_audiences: audiencesIds };
    return dispatch(
      updateLessonsCategoryAudiences(exerciseId, lessonsCategoryId, data),
    );
  };
  const consolidatedAnswers = R.pipe(
    R.groupBy(R.prop('lessons_answer_question')),
    R.toPairs,
    R.map((n) => {
      let totalScore = 0;
      return [
        n[0],
        {
          score: Math.round(
            R.pipe(
              R.map((o) => {
                totalScore += o.lessons_answer_score;
                return totalScore;
              }),
              R.sum,
            )(n[1]) / n[1].length,
          ),
          number: n[1].length,
          comments: R.filter(
            (o) => o.lessons_answer_positive !== null
              || o.lessons_answer_negative !== null,
            n[1],
          ).length,
        },
      ];
    }),
    R.fromPairs,
  )(lessonsAnswers);
  return (
    <div style={{ marginTop: 40 }}>
      {sortedCategories.map((category) => {
        const questions = sortQuestions(
          lessonsQuestions.filter(
            (n) => n.lessons_question_category === category.lessonscategory_id,
          ),
        );
        return (
          <div key={category.lessonscategory_id} style={{ marginTop: 70 }}>
            <Typography variant="h2" style={{ float: 'left' }}>
              {category.lessons_category_name}
            </Typography>
            {!isReport && (
              <LessonsCategoryPopover
                exerciseId={exerciseId}
                lessonsCategory={category}
              />
            )}
            <div className="clearfix" />
            <Grid container={true} spacing={3}>
              <Grid item={true} xs={4} style={{ marginTop: -10 }}>
                <Typography variant="h4">{t('Questions')}</Typography>
                <Paper
                  variant="outlined"
                  classes={{ root: classes.paper }}
                  style={{ marginTop: 14 }}
                >
                  <List style={{ padding: 0 }}>
                    {questions.map((question) => (
                      <ListItem
                        key={question.lessonsquestion_id}
                        divider={true}
                      >
                        <ListItemIcon>
                          <HelpOutlined />
                        </ListItemIcon>
                        <ListItemText
                          style={{ width: '50%' }}
                          primary={question.lessons_question_content}
                          secondary={
                            question.lessons_question_explanation
                            || t('No explanation')
                          }
                        />
                        {!isReport && (
                          <ListItemSecondaryAction>
                            <LessonsQuestionPopover
                              exerciseId={exerciseId}
                              lessonsCategoryId={category.lessonscategory_id}
                              lessonsQuestion={question}
                            />
                          </ListItemSecondaryAction>
                        )}
                      </ListItem>
                    ))}
                    {!isReport && (
                      <CreateLessonsQuestion
                        inline={true}
                        exerciseId={exerciseId}
                        lessonsCategoryId={category.lessonscategory_id}
                      />
                    )}
                  </List>
                </Paper>
              </Grid>
              <Grid item={true} xs={5} style={{ marginTop: -10 }}>
                <Typography variant="h4">{t('Results')}</Typography>
                <Paper
                  variant="outlined"
                  classes={{ root: classes.paper }}
                  style={{ marginTop: 14 }}
                >
                  <List style={{ padding: 0 }}>
                    {questions.map((question) => {
                      const consolidatedAnswer = consolidatedAnswers[
                        question.lessonsquestion_id
                      ] || { score: 0, number: 0, comments: 0 };
                      return (
                        <ListItem
                          key={question.lessonsquestion_id}
                          divider={true}
                          button={true}
                          onClick={() => setSelectedQuestion && setSelectedQuestion(question)
                          }
                        >
                          <ListItemText
                            style={{ width: '50%' }}
                            primary={`${consolidatedAnswer.number} ${t(
                              'answers',
                            )}`}
                            secondary={`${t('of which')} ${
                              consolidatedAnswer.comments
                            } ${t('contain comments')}`}
                          />
                          <Box
                            sx={{
                              display: 'flex',
                              alignItems: 'center',
                              width: '30%',
                              marginRight: 1,
                            }}
                          >
                            <Box sx={{ width: '100%', mr: 1 }}>
                              <LinearProgress
                                variant="determinate"
                                value={consolidatedAnswer.score}
                              />
                            </Box>
                            <Box sx={{ minWidth: 35 }}>
                              <Typography
                                variant="body2"
                                color="text.secondary"
                              >
                                {consolidatedAnswer.score}%
                              </Typography>
                            </Box>
                          </Box>
                        </ListItem>
                      );
                    })}
                  </List>
                </Paper>
              </Grid>
              <Grid item={true} xs={3} style={{ marginTop: -10 }}>
                <Typography variant="h4" style={{ float: 'left' }}>
                  {t('Targeted audiences')}
                </Typography>
                {!isReport && (
                  <LessonsCategoryAddAudiences
                    exerciseId={exerciseId}
                    lessonsCategoryId={category.lessonscategory_id}
                    lessonsCategoryAudiencesIds={
                      category.lessons_category_audiences
                    }
                    handleUpdateAudiences={handleUpdateAudiences}
                  />
                )}
                <div className="clearfix" />
                <Paper
                  variant="outlined"
                  classes={{ root: classes.paperPadding }}
                >
                  {category.lessons_category_audiences.map((audienceId) => {
                    const audience = audiencesMap[audienceId];
                    return (
                      <Tooltip
                        key={audienceId}
                        title={audience?.audience_name || ''}
                      >
                        <Chip
                          onDelete={
                            isReport
                              ? undefined
                              : () => handleUpdateAudiences(
                                category.lessonscategory_id,
                                R.filter(
                                  (n) => n !== audienceId,
                                  category.lessons_category_audiences,
                                ),
                              )
                          }
                          label={truncate(audience?.audience_name || '', 30)}
                          icon={<CastForEducationOutlined />}
                          classes={{ root: classes.chip }}
                        />
                      </Tooltip>
                    );
                  })}
                </Paper>
              </Grid>
            </Grid>
          </div>
        );
      })}
    </div>
  );
};

export default LessonsCategories;