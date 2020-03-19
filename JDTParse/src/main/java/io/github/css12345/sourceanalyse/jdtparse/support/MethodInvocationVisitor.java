package io.github.css12345.sourceanalyse.jdtparse.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation;
import io.github.css12345.sourceanalyse.jdtparse.exception.BindingResolveException;

/**
 * A visitor to acquire the method invocation you really want.
 * <p>
 * For example, there may exist two class, they have the same name and same
 * method name, but different package.<br>
 * Assume one is p1.A, the other is p2.A, they both have a method "public void
 * b() {//different logical}",<br>
 * you may call the method by:
 * 
 * <pre class="code">
 * package test;
 * import p1.A;
 * 
 * public Class Example {
 *   public static void main (String[] args) {
 *     A a = new A();
 *     a.b();
 *   }
 * }
 * </pre>
 * 
 * Which A's b method will be called depends on your import.
 * </p>
 * <p>
 * You may want to know all method invocations in a method, but only the called
 * object is in specific packages.<br>
 * Then add the package name strings you want to the field wantedPackageNames,
 * use {@code ASTNode.accept(ASTVisitor visitor)}, pass this visitor as parameter, then
 * you will get method invocations you really want in field
 * wantedMethodInvocations.<br>
 * If you don't set wantedPackageNames, all method invocations will be added.
 * </p>
 */
public class MethodInvocationVisitor extends ASTVisitor {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * If the called object's package is start with the string in set, this
	 * invocation will be added to wantedMethodInvocations.<br>
	 * If this set is empty, directly add it.
	 */
	private Set<String> wantedPackageNames = new HashSet<>();

	/**
	 * The final invocations
	 */
	private List<MethodInvocation> wantedMethodInvocations = new ArrayList<>();

	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		if (binding != null) {			
			ITypeBinding declaringClassTypeBinding = binding.getDeclaringClass();

			String qualifiedName = declaringClassTypeBinding.getQualifiedName();
			
			//the invocated class must also be resolved
			if (!FileInformation.getClassQualifiedNameLocationMap().containsKey(qualifiedName)) {
				return true;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("{}'s declaring class qualified name is {}", node, qualifiedName);
			}
			
			if (ObjectUtils.isEmpty(wantedPackageNames))
				wantedMethodInvocations.add(node);
			else {
				for (String name : wantedPackageNames)
					if (qualifiedName.startsWith(name)) {
						wantedMethodInvocations.add(node);
						break;
					}
			}
		} else {
			String errorMessage = "an error occur when resolve method binding of " + node;
			throw new BindingResolveException(errorMessage);
		}
		return true;

	}

	public Set<String> getWantedPackageNames() {
		return wantedPackageNames;
	}

	public void setWantedPackageNames(Set<String> wantedPackageNames) {
		this.wantedPackageNames = wantedPackageNames;
	}

	public List<MethodInvocation> getWantedMethodInvocations() {
		return wantedMethodInvocations;
	}

}
