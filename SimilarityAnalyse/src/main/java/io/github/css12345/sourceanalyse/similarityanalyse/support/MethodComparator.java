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
import io.github.css12345.sourceanalyse.similarityanalyse.entity.Graph;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.State;
import io.github.css12345.sourceanalyse.similarityanalyse.utils.GraphSimilarityCalculator;

@Component
public class MethodComparator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ASTNodeRepository astNodeRepository;

	public static final double THRESHOLD = 1e-6;

	/**
	 * this method is aim to set state of argument methodCompare, if field
	 * {@linkplain MethodCompare#method1 method1} and field
	 * {@linkplain MethodCompare#method2 method2} are not null, will calculate their
	 * similarity.
	 * 
	 * @param methodCompare must set the compared methods
	 */
	public void compare(MethodCompare methodCompare) {
		Method method1 = methodCompare.getMethod1();
		Method method2 = methodCompare.getMethod2();

		if (logger.isDebugEnabled()) {
			logger.debug("start to compare method1 and method2 of brief method information {}",
					method1 != null ? method1.getBriefMethodInformation() : method2.getBriefMethodInformation());
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
				double similarity = GraphSimilarityCalculator.calculate(graph1, graph2);
				methodCompare.setSimilarity(similarity);

				if (Math.abs(similarity - 1) <= THRESHOLD)
					methodCompare.setState(State.UNMODIFIED);
				else
					methodCompare.setState(State.MODIFIED);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(
					"compare method1 and method2 of brief method information {} finished, state is {}, similarity is {}",
					method1 != null ? method1.getBriefMethodInformation() : method2.getBriefMethodInformation(),
					methodCompare.getState(), methodCompare.getSimilarity());
		}
	}

	public Graph buildGraph(Method method) {
		Map<Long, List<Long>> edges = new HashMap<>();
		Map<Long, String> nodeLabels = new HashMap<>();
		ASTNode parentNode = method.getRootNode();

		buildEdgesAndLabels(parentNode, edges, nodeLabels);
		Graph graph = new Graph(edges, nodeLabels);
		return graph;
	}

	private void buildEdgesAndLabels(ASTNode parentNode, Map<Long, List<Long>> edges, Map<Long, String> labels) {
		labels.put(parentNode.getId(), parentNode.getType());
		List<ASTNode> childNodes = astNodeRepository.findChildNodesByParentNodeId(parentNode.getId());
		if (childNodes.size() > 0) {
			List<Long> idOfChilds = childNodes.stream().map(childNode -> childNode.getId())
					.collect(Collectors.toList());
			edges.put(parentNode.getId(), idOfChilds);
			for (ASTNode childNode : childNodes) {
				buildEdgesAndLabels(childNode, edges, labels);
			}
		}
	}
}
