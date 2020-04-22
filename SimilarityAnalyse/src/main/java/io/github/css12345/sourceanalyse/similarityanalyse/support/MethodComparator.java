package io.github.css12345.sourceanalyse.similarityanalyse.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.persistence.entity.ASTNode;
import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.repository.ASTNodeRepository;
import io.github.css12345.sourceanalyse.persistence.repository.MethodRepository;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.Graph;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.State;
import io.github.css12345.sourceanalyse.similarityanalyse.utils.FileCompareCacheUtils;
import io.github.css12345.sourceanalyse.similarityanalyse.utils.GraphSimilarityCalculator;

@Component
public class MethodComparator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ASTNodeRepository astNodeRepository;

	@Autowired
	private MethodRepository methodRepository;

	@Autowired
	private GraphSimilarityCalculator graphSimilarityCalculator;

	public static final double THRESHOLD = 1e-6;

	/**
	 * this method is aim to set state of argument methodCompare, if field
	 * {@linkplain MethodCompare#briefMethodInformation1 briefMethodInformation1}
	 * and field {@linkplain MethodCompare#briefMethodInformation2
	 * briefMethodInformation2} are not null, will calculate their similarity.
	 * 
	 * @param methodCompare must set the compared methods
	 */
	public void compare(MethodCompare methodCompare) {
		if (FileCompareCacheUtils.contains(methodCompare.getBriefMethodInformation1(), methodCompare.getVersion1(),
				methodCompare.getBriefMethodInformation2(), methodCompare.getVersion2())) {
			MethodCompare cachedMethodCompare = FileCompareCacheUtils.getMethodCompare(
					methodCompare.getBriefMethodInformation1(), methodCompare.getVersion1(),
					methodCompare.getBriefMethodInformation2(), methodCompare.getVersion2());
			methodCompare.setState(cachedMethodCompare.getState());
			methodCompare.setSimilarity(cachedMethodCompare.getSimilarity());

			if (logger.isDebugEnabled()) {
				logger.debug(
						"find cached methodCompare for method1[briefMethodInformation={},version={}] and method2[briefMethodInformation={},version={}]",
						methodCompare.getBriefMethodInformation1(), methodCompare.getVersion1(),
						methodCompare.getBriefMethodInformation2(), methodCompare.getVersion2());
			}
			return;
		}

		Method method1 = methodRepository.findByBriefMethodInformationAndVersion(
				methodCompare.getBriefMethodInformation1(), methodCompare.getVersion1());

		Method method2 = methodRepository.findByBriefMethodInformationAndVersion(
				methodCompare.getBriefMethodInformation2(), methodCompare.getVersion2());

		if (logger.isDebugEnabled()) {
			logger.debug(
					"start to compare method1[briefMethodInformation={},version={}] and method2[briefMethodInformation={},version={}]",
					methodCompare.getBriefMethodInformation1(), methodCompare.getVersion1(),
					methodCompare.getBriefMethodInformation2(), methodCompare.getVersion2());
		}

		if (method1 == null && method2 != null)
			methodCompare.setState(State.ADD);
		else if (method1 != null && method2 == null)
			methodCompare.setState(State.DELETE);
		else {
			Graph graph1 = buildGraph(method1);
			Graph graph2 = buildGraph(method2);

			if (graph1.getNodeLabels().size() == 1 || graph2.getNodeLabels().size() == 1) {
				if (graph1.getNodeLabels().size() == 1 && graph2.getNodeLabels().size() == 1) {
					methodCompare.setState(State.UNMODIFIED);
					methodCompare.setSimilarity(1);
				} else {
					methodCompare.setState(State.MODIFIED);
				}
			} else {
				double similarity = graphSimilarityCalculator.calculate(graph1, graph2);
				methodCompare.setSimilarity(similarity);

				if (Math.abs(similarity - 1) <= THRESHOLD)
					methodCompare.setState(State.UNMODIFIED);
				else
					methodCompare.setState(State.MODIFIED);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(
					"compare method1[briefMethodInformation={},version={}] and method2[briefMethodInformation={},version={}] finished, state is {}, similarity is {}",
					methodCompare.getBriefMethodInformation1(), methodCompare.getVersion1(),
					methodCompare.getBriefMethodInformation2(), methodCompare.getVersion2(), methodCompare.getState(),
					methodCompare.getSimilarity());
		}
	}

	public Graph buildGraph(Method method) {
		Map<String, List<String>> edges = new HashMap<>();
		Map<String, String> nodeLabels = new HashMap<>();
		ASTNode parentNode = method.getRootNode();

		buildEdgesAndLabels(parentNode, edges, nodeLabels);
		Graph graph = new Graph(edges, nodeLabels);
		return graph;
	}

	private void buildEdgesAndLabels(ASTNode parentNode, Map<String, List<String>> edges, Map<String, String> labels) {
		labels.put(parentNode.getId(), parentNode.getType());
		List<ASTNode> childNodes = astNodeRepository.findChildNodesByParentNodeId(parentNode.getId());
		if (childNodes.size() > 0) {
			List<String> idOfChilds = childNodes.stream().map(childNode -> childNode.getId())
					.collect(Collectors.toList());
			edges.put(parentNode.getId(), idOfChilds);
			for (ASTNode childNode : childNodes) {
				buildEdgesAndLabels(childNode, edges, labels);
			}
		}
	}
}
