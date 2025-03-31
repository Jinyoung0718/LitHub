package com.sjy.LitHub.file.entity;

import java.util.Objects;

import com.sjy.LitHub.global.config.AppConfig;
import com.sjy.LitHub.global.entity.BaseTime;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class GenFile extends BaseTime {

	private int fileNo;
	private String originalFileName;
	private String directoryPath;
	private String fileExt;
	private String fileExtTypeCode;
	private String fileName;
	private long fileSize;

	@Override
	public boolean equals(Object o) {
		if (getId() != null) return super.equals(o);
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GenFile that = (GenFile) o;
		return fileNo == that.getFileNo() && Objects.equals(getTypeCodeAsStr(), that.getTypeCodeAsStr());
	}

	@Override
	public int hashCode() {
		if (getId() != null) return super.hashCode();
		return Objects.hash(super.hashCode(), getTypeCodeAsStr(), fileNo);
	}

	public String getFilePath() {
		return AppConfig.getFileUploadDir()
			+ "/gen/"
			+ getModelName()
			+ "/" + directoryPath
			+ "/" + fileName;
	}

	public String getPublicUrl() {
		return AppConfig.getSiteBackUrl()
			+ "/gen/"
			+ getModelName()
			+ "/" + getOwnerModelId()
			+ "/" + fileName;
	}

	abstract protected long getOwnerModelId();

	abstract protected String getTypeCodeAsStr();
}