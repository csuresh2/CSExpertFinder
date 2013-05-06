import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;


public class Query
{
	protected String queryString;
	protected String currentCore;
	protected SearchResult result;

	public Query(String queryString, SearchResult result)
	{
		this.queryString = queryString;
		this.currentCore = "concept";
		this.result = result;
	}

	/**
	 * Query string getter method.
	 * @return
	 */
	public String getQueryString()
	{
		return queryString;
	}

	/**
	 * Sets currentCore for Solr server initialization.
	 * @param currentCore
	 */
	public void setCurrentCore(String currentCore)
	{
		this.currentCore = currentCore;
	}

	/**
	 * Returns search result.
	 * @return
	 */
	public SearchResult getSearchResult()
	{
		return result;
	}

	/**
	 * Default type of search is concept search mechanism.
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public void runSearch() throws IOException, SolrServerException
	{
		System.out.println("\nResearch concept search started...\n");
		result.appendResultString("\nResearch concept search started...\n");
		setCurrentCore("people");
		runSearch(queryString);
		result.appendResultString("Research concept search completed.\n");
		System.out.println("Research concept search completed.\n");
	}

	/**
	 * Generate_concept_queries.
	 * @param concept_name the concept_name
	 * @return the string[] - set of queries
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String[] generateConceptQueries(String conceptName) throws IOException
	{
		String[] queries = new String[ExpertFinder.MAX_CONCEPT_QUERIES];
		String[] terms = new String[ExpertFinder.MAX_CONCEPT_QUERIES];
		String queryString = "";
		int i =0;
		File file = new File(ExpertFinder.solrConceptsDocumentsPath + "/" + conceptName);
		terms = FileUtilities.getFieldListFromTabularData(file, 2);

		for(String str:terms)
		{
			if(i==0)
			{
				queryString = str;
			}
			else
			{
				queryString += " ";
				queryString += str;				
			}
			queries[i] = queryString;
			i++;
		}

		return queries;
	}

	/**
	 * Search_documents.
	 * Search for the input query and return the Solr Doc list
	 * @return the solr document list
	 * @throws SolrServerException the solr server exception
	 */
	public SolrDocumentList searchDocuments(String queryString) throws SolrServerException
	{
		SolrServer server = new HttpSolrServer(ExpertFinder.SOLR_URL + currentCore);
	    SolrQuery query = new SolrQuery();
	    query.setIncludeScore(true);
	    query.setQuery(queryString);
	    QueryResponse response = server.query( query );
		SolrDocumentList docs = response.getResults();
		return docs;
	}

	/**
	 * Gets the _top_docs_with_scores.
	 * Search the input query and return relevancy Map with decreasing relevancy scores
	 * @param query_string the queryString
	 * @return the top documents with scores
	 * @throws SolrServerException the solr server exception
	 */
	public Map<String, String> getTopDocsWithScores(String queryString) throws SolrServerException
	{
		Map<String,String> topDocumentsMap=new HashMap<String, String>();
		SolrDocumentList docs = searchDocuments(queryString);
		for (int i = 0; i < docs.size(); ++i)
		{
			topDocumentsMap.put(docs.get(i).getFieldValue("id").toString(), 
				docs.get(i).getFirstValue("score").toString());
		}

		return topDocumentsMap;
	}

	/**
	 * Run concept search.
	 * Perform concept based search for find experts. Default search is 
	 * concept based search.
	 * @param concept the concept
	 * @return the array list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SolrServerException the solr server exception
	 */
	public ArrayList<Map<String, String>> runSearch(String concept) throws IOException, SolrServerException
	{
		result.appendResultString("\nResearch concept string:\n" + concept);
		ArrayList<Map<String, String>> relevancyMapList = new ArrayList<Map<String, String>>();
		Map<String,String> topDocumentsMap;
		Map<String,Double> finalWeightedMap;
		
		result.appendResultString("\n\nResearch concept search results:\n\n");
		String[] conceptQueries = generateConceptQueries(concept);

		for(String conceptQuery:conceptQueries)
		{
			topDocumentsMap = getTopDocsWithScores(conceptQuery);
			relevancyMapList.add(topDocumentsMap);
		}

		finalWeightedMap = result.scoreByLinearDecreasingWeighting(relevancyMapList, 1);
		result.appendResultString("Person Name\tWeighted Relevancy score for concept queries\n");

		Iterator<String> iterator = finalWeightedMap.keySet().iterator();  	   
		while (iterator.hasNext())
		{  
			String key = iterator.next();  
			String value = finalWeightedMap.get(key).toString();     
			result.appendResultString(key + "\t" + value + "\n");
		}

		result.appendResultString("\n");
		return relevancyMapList;
	}
}
