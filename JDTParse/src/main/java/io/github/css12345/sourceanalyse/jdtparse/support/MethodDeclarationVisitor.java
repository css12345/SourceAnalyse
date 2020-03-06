package io.github.css12345.sourceanalyse.jdtparse.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.css12345.sourceanalyse.jdtparse.entity.MethodInformation;

/**
 * A visitor to acquire method informations.
 */
public class MethodDeclarationVisitor extends ASTVisitor {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @see MethodInvocationVisitor#wantedPackageNames
	 */
	private Set<String> wantedPackageNames = new HashSet<>();

	/**
	 * A method declaration have many type ASTNode, if the type is in set, we save
	 * it in methodInformation object, else we don't save.<br>
	 * If includedNodeTypes is empty, we directly save it.
	 */
	private Set<String> includedNodeTypes = new HashSet<>();

	/**
	 * By call {@link ASTNode#accept(ASTVisitor visitor)}, pass this visitor as
	 * parameter, all method information of call node will be saved in this field.
	 */
	private List<MethodInformation> methodInformations = new ArrayList<>();

	@Override
	public boolean visit(MethodDeclaration methodDeclaration) {
		MethodInformation methodInformation = new MethodInformation();
		methodInformation.setMethodDeclaration(methodDeclaration);
		
//		IMethodBinding binding = methodDeclaration.resolveBinding();
//		if (binding != null) {
//			ITypeBinding declaringClassTypeBinding = binding.getDeclaringClass();
//			String qualifiedName = declaringClassTypeBinding.getQualifiedName();
//			methodInformation.setDeclaringClassQualifiedName(qualifiedName);
//		} else {
//			String errorMessage = "an error occur when resolve binding of " + methodDeclaration;
//			throw new BindingResolveException(errorMessage);
//		}

		
		methodInformation.setMethodInvocations(getMethodInvocations(methodDeclaration));
		
		setNodes(methodDeclaration, methodInformation);
		
		setEdges(methodInformation);
		
		methodInformations.add(methodInformation);
		
		if (logger.isDebugEnabled()) {
			logger.debug("add {} for method information list", methodInformation);
		}
		return true;
	}

	private void setEdges(MethodInformation methodInformation) {
		List<List<Integer>> edges = methodInformation.getEdges();
		List<ASTNode> nodes = methodInformation.getNodes();
		for (int i = 0; i < nodes.size(); i++)
			edges.add(new ArrayList<>());

		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).getParent().equals(nodes.get(i))) {
					edges.get(i).add(j);
				}
			}
		}
	}

	private void setNodes(MethodDeclaration methodDeclaration, MethodInformation methodInformation) {
		ASTVisitor nodeVisitor = new ASTVisitor() {
			@Override
			public void postVisit(ASTNode node) {
				checkAndAddNode(methodInformation, node);
			}

			public void checkAndAddNode(MethodInformation methodInformation, ASTNode node) {
				if (ObjectUtils.isEmpty(includedNodeTypes))
					methodInformation.addNode(node);
				else {
					if (includedNodeTypes.contains(node.getClass().getSimpleName()))
						methodInformation.addNode(node);
				}
			}
		};
		methodDeclaration.accept(nodeVisitor);
	}

	private List<MethodInvocation> getMethodInvocations(MethodDeclaration methodDeclaration) {
		MethodInvocationVisitor visitor = new MethodInvocationVisitor();
		visitor.setWantedPackageNames(wantedPackageNames);
		methodDeclaration.accept(visitor);
		return visitor.getWantedMethodInvocations();
	}

	public List<MethodInformation> getMethodInformations() {
		return methodInformations;
	}

	public void setValidPackageNames(Set<String> validPackageNames) {
		this.wantedPackageNames = validPackageNames;
	}

	public Set<String> getValidPackageNames() {
		return wantedPackageNames;
	}

	public Set<String> getIncludedNodeTypes() {
		return includedNodeTypes;
	}

	public void setIncludedNodeTypes(Set<String> includedNodeTypes) {
		this.includedNodeTypes = includedNodeTypes;
	}
}
