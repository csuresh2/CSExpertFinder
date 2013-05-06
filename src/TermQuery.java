import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;


public class TermQuery extends Query
{
	/**
	 * Initialize term query object with the query string.
	 * @param queryString
	 * @throws IOException
	 */
	public TermQuery(String queryString, SearchResult result)
	{
		super(queryString, result);
	}

	/**
	 * Perform term query search.
	 */
	public void runSearch() throws SolrServerException, IOException
	{
		System.out.println("\nInput terms search started...\n");
		result.appendResultString("\nInput terms search started...\n");
		setCurrentCore("people");
		runTermSearch();	
		System.out.println("Input terms search completed.\n");
		result.appendResultString("Input terms search completed.\n");
	}

	/**
	 * Run_term_search.
	 * Perform term based search to find experts
	 * @return the string
	 * @throws SolrServerException the solr server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void runTermSearch() throws SolrServerException, IOException
	{
		ArrayList<Map<String, String>> finalRelevancyList = new ArrayList<Map<String, String>>();
		ArrayList<Map<String, String>> conceptRelevancyList = null;
		Map<String,Double> finalWeightedMap;
		Map<String, String> relevantConcepts;
		Map.Entry<String, String> bestRelevantConcept;

		setCurrentCore("concept");
		relevantConcepts = getTopDocsWithScores(queryString);
		bestRelevantConcept = null;

		if(relevantConcepts.size() > 0)
		{
			for (Map.Entry<String, String> entry : relevantConcepts.entrySet())
			{
				if (bestRelevantConcept == null || entry.getValue().compareTo(
						bestRelevantConcept.getValue()) > 0)
				{
					bestRelevantConcept = entry;
				}
			}

			setCurrentCore("people");
			// Run concept based search
			conceptRelevancyList = runSearch(bestRelevantConcept.getKey().toString());	
		}

		Map<String, String> termRelevancyMap = getTopDocsWithScores(queryString);

		result.appendResultString("\nInput term search string:\nqueryString\n\nMost " +
			"relevant concept to input terms:\n");

		if(relevantConcepts.size() > 0)
			result.appendResultString(bestRelevantConcept.getKey().toString());		

		result.appendResultString("\n\nInput terms search results:\n");
		finalRelevancyList.add(termRelevancyMap);

		if(relevantConcepts.size() > 0)
			finalRelevancyList.addAll(conceptRelevancyList);

		finalWeightedMap = result.scoreByLinearDecreasingWeighting(finalRelevancyList, 2);
		Iterator<String> iterator = finalWeightedMap.keySet().iterator();  	   
		result.appendResultString("Person Name\t" + "Weighted Relevancy score for " +
			"term/concept queries\n");

		while (iterator.hasNext())
		{  
			String key = iterator.next();  
			String value = finalWeightedMap.get(key).toString();     
			result.appendResultString(key + "\t" + value + "\n");
		}

		result.appendResultString("\n");	
	}
}
