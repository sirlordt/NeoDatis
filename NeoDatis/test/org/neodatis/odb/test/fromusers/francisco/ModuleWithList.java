package org.neodatis.odb.test.fromusers.francisco;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neodatis.odb.impl.tool.UUID;

public class ModuleWithList implements Serializable {

	// @Dependency private TagRepository topicRepo;

	private Long id = UUID.getRandomLongId();
	private String title;
	private String description;
	private Date dateTime = new Date();
	private String author;
	private String homePageUrl;
	private String examplesUrl;
	private String sourceCodeUrl;
	private String sourceCodeExamplesUrl;
	private String mavenArtifactUrl;
	private Integer votes = 0;
	private List<Tag> topics = new ArrayList<Tag>();
	private List<Version> versions = new ArrayList<Version>();

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public List<Tag> getTopics() {
		return topics;
	}

	public void setTopics(List<Tag> topics) {
		this.topics = topics;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public void addTopic(Tag topic) {
		// topicRepo.save(topic);
		getTopics().add(topic);
	}

	public String getHomePageUrl() {
		return homePageUrl;
	}

	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}

	public String getExamplesUrl() {
		return examplesUrl;
	}

	public void setExamplesUrl(String examplesUrl) {
		this.examplesUrl = examplesUrl;
	}

	public String getSourceCodeUrl() {
		return sourceCodeUrl;
	}

	public void setSourceCodeUrl(String sourceCodeUrl) {
		this.sourceCodeUrl = sourceCodeUrl;
	}

	public String getSourceCodeExamplesUrl() {
		return sourceCodeExamplesUrl;
	}

	public void setSourceCodeExamplesUrl(String sourceCodeExamplesUrl) {
		this.sourceCodeExamplesUrl = sourceCodeExamplesUrl;
	}

	public String getMavenArtifactUrl() {
		return mavenArtifactUrl;
	}

	public void setMavenArtifactUrl(String mavenArtifactUrl) {
		this.mavenArtifactUrl = mavenArtifactUrl;
	}

	public Integer getVotes() {
		return votes;
	}

	public void addVote() {
		votes++;
	}

	public List<Version> getVersions() {
		return versions;
	}

	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ModuleWithList)) {
			return false;
		}
		return ((ModuleWithList) obj).getId().equals(this.getId());
	}

	// public static enum Version {
	//		
	// _1_2("1.2"), _1_3("1.3"), _1_4("1.4");
	//		
	// private String s;
	//		
	// private Version(String s) {
	// this.s = s;
	// }
	//		
	// @Override
	// public String toString() {
	// return s;
	// }
	//		
	// }

}