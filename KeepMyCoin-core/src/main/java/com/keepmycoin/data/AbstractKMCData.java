package com.keepmycoin.data;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.keepmycoin.Configuration;
import com.keepmycoin.utils.KMCStringUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractKMCData {
	@JsonProperty("msgWarning")
	private String msgWarning;

	@JsonProperty("kmcVersion")
	private double kmcVersion;

	@JsonProperty("jvm")
	private String jvm;

	@JsonProperty("creationDate")
	private String creationDate;

	@JsonProperty("dataType")
	private String dataType;

	@JsonGetter("msgWarning")
	public String getMsgWarning() {
		return "DO NOT change content of this file, any modification will corrupt this file and LOSING data FOREVER!";
	}

	@JsonSetter("msgWarning")
	public void setMsgWarning(String msgWarning) {
		// this.msgWarning = msgWarning;
	}

	@JsonGetter("kmcVersion")
	public double getKmcVersion() {
		return this.kmcVersion;
	}

	@JsonSetter("kmcVersion")
	public void setKmcVersion(double kmcVersion) {
		this.kmcVersion = kmcVersion;
	}

	@JsonGetter("jvm")
	public String getJvm() {
		return this.jvm;
	}

	@JsonSetter("jvm")
	public void setJvm(String jvm) {
		this.jvm = jvm;
	}

	@JsonGetter("creationDate")
	public String getCreationDate() {
		return this.creationDate;
	}

	@JsonSetter("creationDate")
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	@JsonGetter("dataType")
	public String getDataType() {
		return this.dataType;
	}

	@JsonSetter("dataType")
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@JsonIgnore
	public void addAdditionalInformation() {
		this.msgWarning = getMsgWarning();
		this.kmcVersion = Configuration.APP_VERSION;
		this.jvm = Runtime.class.getPackage().getImplementationVersion();
		this.creationDate = KMCStringUtil.convertDateTimeToString(new Date());
		this.dataType = this.getClass().getSimpleName();
	}

	@JsonIgnore
	protected String encodeBuffer(byte[] buffer) {
		return buffer == null ? null : Base64.getEncoder().encodeToString(buffer);
	}

	@JsonIgnore
	protected byte[] decodeToBuffer(String data) {
		return data == null ? null : Base64.getDecoder().decode(data);
	}

	@JsonIgnore
	@SuppressWarnings("unchecked")
	public static <T extends AbstractKMCData> List<T> filter(List<AbstractKMCData> files, Class<T> clz) {
		return (List<T>) files.stream().filter(f -> f != null && f.getClass().equals(clz)).collect(Collectors.toList());
	}
}
