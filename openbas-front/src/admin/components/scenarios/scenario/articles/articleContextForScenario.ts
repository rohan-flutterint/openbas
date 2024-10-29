import type { ArticleStore, FullArticleStore } from '../../../../../actions/channels/Article';
import { addScenarioArticle, deleteScenarioArticle, updateScenarioArticle } from '../../../../../actions/channels/article-action';
import type { ScenarioStore } from '../../../../../actions/scenarios/Scenario';
import type { ArticleCreateInput, ArticleUpdateInput } from '../../../../../utils/api-types';
import { useAppDispatch } from '../../../../../utils/hooks';

const articleContextForScenario = (scenarioId: ScenarioStore['scenario_id']) => {
  const dispatch = useAppDispatch();
  return {
    previewArticleUrl: (article: FullArticleStore) => `/channels/${scenarioId}/${article.article_fullchannel?.channel_id}?preview=true`,
    onAddArticle: (data: ArticleCreateInput) => dispatch(addScenarioArticle(scenarioId, data)),
    onUpdateArticle: (article: ArticleStore, data: ArticleUpdateInput) => dispatch(
      updateScenarioArticle(scenarioId, article.article_id, data),
    ),
    onDeleteArticle: (article: ArticleStore) => dispatch(
      deleteScenarioArticle(scenarioId, article.article_id),
    ),
  };
};

export default articleContextForScenario;