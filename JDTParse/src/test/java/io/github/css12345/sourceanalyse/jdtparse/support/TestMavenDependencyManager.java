package io.github.css12345.sourceanalyse.jdtparse.support;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

public class TestMavenDependencyManager {

	private DependencyManager manager;

	@BeforeEach
	public void setUp() {
		manager = new MavenDependencyManager();
	}

	@Test
	public void testOneModuleDependencies() throws IOException {
		String pathname = "D:\\Users\\cs\\Documents\\javaSourceCode\\druid-3cd92eca5faa5d584c6341111fccba7663213ecc";
		File projectRootFile = new File(pathname);
		Project project = new Project();
		project.setPath(pathname);
		manager.configureProjectDependencies(project);
		List<String> pathOfDependencies = project.getPathOfDependencies();
		assertEquals(136, pathOfDependencies.size());
		assertFalse(new File(projectRootFile.getParentFile(), "result.txt").exists());
	}

	@Test
	public void testMultiModulesDependencies() {
		String pathname = "D:\\Users\\cs\\Documents\\GitHub\\oim-fx\\oim-client-parent";
		Project project = new Project();
		project.setPath(pathname);
		project.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("com.only"))));
		manager.configureProjectDependencies(project);
		assertEquals(0, project.getPathOfDependencies().size());
		assertEquals(3, project.getModules().size());
		
		assertEquals(7,
				project.findModule("D:\\Users\\cs\\Documents\\GitHub\\oim-fx\\oim-client-base").getPathOfDependencies().size());
		assertEquals(10, project.findModule("D:\\Users\\cs\\Documents\\GitHub\\oim-fx\\oim-client-common").getPathOfDependencies().size());
		
		assertEquals(142,
				project.findModule("D:\\Users\\cs\\Documents\\GitHub\\oim-fx\\oim-client-core").getPathOfDependencies().size());
		
		assertTrue(project.findModule("D:\\Users\\cs\\Documents\\GitHub\\oim-fx\\oim-client-base").getWantedPackageNames().contains("com.only"));
	}
	
	@Test
	public void testOtherMultiModulesDependencies() {
		String pathname = "D:\\Users\\cs\\Documents\\javaSourceCode\\Sentinel-master";
		Project project = new Project();
		project.setPath(pathname);
		manager.configureProjectDependencies(project);
		assertEquals(0, project.getPathOfDependencies().size());
		assertEquals(8, project.getModules().size());

		
		
		
		Project sentinel_adapter = project.findModule(new File(pathname, "sentinel-adapter").getAbsolutePath());
		assertEquals(0, sentinel_adapter.getPathOfDependencies().size());
		assertEquals(11, sentinel_adapter.getModules().size());
		
		Project sentinel_apache_dubbo_adapter = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-apache-dubbo-adapter").getAbsolutePath());
		assertEquals(19, sentinel_apache_dubbo_adapter.getPathOfDependencies().size());
		assertEquals(0, sentinel_apache_dubbo_adapter.getModules().size());
		
		Project sentinel_api_gateway_adapter_common = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-api-gateway-adapter-common").getAbsolutePath());
		assertEquals(12, sentinel_api_gateway_adapter_common.getPathOfDependencies().size());
		assertEquals(0, sentinel_api_gateway_adapter_common.getModules().size());
		
		Project sentinel_dubbo_adapter = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-dubbo-adapter").getAbsolutePath());
		assertEquals(16, sentinel_dubbo_adapter.getPathOfDependencies().size());
		assertEquals(0, sentinel_dubbo_adapter.getModules().size());
		
		Project sentinel_grpc_adapter = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-grpc-adapter").getAbsolutePath());
		assertEquals(32, sentinel_grpc_adapter.getPathOfDependencies().size());
		assertEquals(0, sentinel_grpc_adapter.getModules().size());
		
		Project sentinel_reactor_adapter = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-reactor-adapter").getAbsolutePath());
		assertEquals(5, sentinel_reactor_adapter.getPathOfDependencies().size());
		assertEquals(0, sentinel_reactor_adapter.getModules().size());
		
		Project sentinel_sofa_rpc_adapter = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-sofa-rpc-adapter").getAbsolutePath());
		assertEquals(47, sentinel_sofa_rpc_adapter.getPathOfDependencies().size());
		assertEquals(0, sentinel_sofa_rpc_adapter.getModules().size());
		
		Project sentinel_spring_cloud_gateway_adapter = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-spring-cloud-gateway-adapter").getAbsolutePath());
		assertEquals(84, sentinel_spring_cloud_gateway_adapter.getPathOfDependencies().size());
		assertEquals(0, sentinel_spring_cloud_gateway_adapter.getModules().size());
		
		Project sentinel_spring_webflux_adapter = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-spring-webflux-adapter").getAbsolutePath());
		assertEquals(71, sentinel_spring_webflux_adapter.getPathOfDependencies().size());
		assertEquals(0, sentinel_spring_webflux_adapter.getModules().size());
		
		Project sentinel_spring_webmvc_adapter = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-spring-webmvc-adapter").getAbsolutePath());
		assertEquals(58, sentinel_spring_webmvc_adapter.getPathOfDependencies().size());
		assertEquals(0, sentinel_spring_webmvc_adapter.getModules().size());
		
		Project sentinel_web_servlet = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-web-servlet").getAbsolutePath());
		assertEquals(51, sentinel_web_servlet.getPathOfDependencies().size());
		assertEquals(0, sentinel_web_servlet.getModules().size());
		
		Project sentinel_zuul_adapter = sentinel_adapter.findModule(new File(sentinel_adapter.getPath(), "sentinel-zuul-adapter").getAbsolutePath());
		assertEquals(97, sentinel_zuul_adapter.getPathOfDependencies().size());
		assertEquals(0, sentinel_zuul_adapter.getModules().size());
		
		
		
		
		Project sentinel_benchmark = project.findModule(new File(pathname, "sentinel-benchmark").getAbsolutePath());
		assertEquals(5, sentinel_benchmark.getPathOfDependencies().size());
		assertEquals(0, sentinel_benchmark.getModules().size());
		
		
		
		
		Project sentinel_cluster = project.findModule(new File(pathname, "sentinel-cluster").getAbsolutePath());
		assertEquals(0, sentinel_cluster.getPathOfDependencies().size());
		assertEquals(4, sentinel_cluster.getModules().size());
		
		Project sentinel_cluster_client_default = sentinel_cluster.findModule(new File(sentinel_cluster.getPath(), "sentinel-cluster-client-default").getAbsolutePath());
		assertEquals(36, sentinel_cluster_client_default.getPathOfDependencies().size());
		assertEquals(0, sentinel_cluster_client_default.getModules().size());
		
		Project sentinel_cluster_common_default = sentinel_cluster.findModule(new File(sentinel_cluster.getPath(), "sentinel-cluster-common-default").getAbsolutePath());
		assertEquals(1, sentinel_cluster_common_default.getPathOfDependencies().size());
		assertEquals(0, sentinel_cluster_common_default.getModules().size());
		
		Project sentinel_cluster_server_default = sentinel_cluster.findModule(new File(sentinel_cluster.getPath(), "sentinel-cluster-server-default").getAbsolutePath());
		assertEquals(36, sentinel_cluster_server_default.getPathOfDependencies().size());
		assertEquals(0, sentinel_cluster_server_default.getModules().size());
		
		Project sentinel_cluster_server_envoy_rls = sentinel_cluster.findModule(new File(sentinel_cluster.getPath(), "sentinel-cluster-server-envoy-rls").getAbsolutePath());
		assertEquals(45, sentinel_cluster_server_envoy_rls.getPathOfDependencies().size());
		assertEquals(0, sentinel_cluster_server_envoy_rls.getModules().size());
		
		
		
		
		Project sentinel_core = project.findModule(new File(pathname, "sentinel-core").getAbsolutePath());
		assertEquals(16, sentinel_core.getPathOfDependencies().size());
		assertEquals(0, sentinel_core.getModules().size());
		
		Project sentinel_dashboard = project.findModule(new File(pathname, "sentinel-dashboard").getAbsolutePath());
		assertEquals(95, sentinel_dashboard.getPathOfDependencies().size());
		assertEquals(0, sentinel_dashboard.getModules().size());
		
		Project sentinel_demo = project.findModule(new File(pathname, "sentinel-demo").getAbsolutePath());
		assertEquals(1, sentinel_demo.getPathOfDependencies().size());
		assertEquals(20, sentinel_demo.getModules().size());
		
		Project sentinel_extension = project.findModule(new File(pathname, "sentinel-extension").getAbsolutePath());
		assertEquals(0, sentinel_extension.getPathOfDependencies().size());
		assertEquals(10, sentinel_extension.getModules().size());
		
		Project sentinel_transport = project.findModule(new File(pathname, "sentinel-transport").getAbsolutePath());
		assertEquals(0, sentinel_transport.getPathOfDependencies().size());
		assertEquals(3, sentinel_transport.getModules().size());
	}
}
