package com.sjy.LitHub.record.repository.group;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.model.res.StudyGroupHistoryDTO;
import com.sjy.LitHub.account.model.res.UserBriefDTO;
import com.sjy.LitHub.file.entity.QUserGenFile;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.record.entity.QStudyGroup;
import com.sjy.LitHub.record.entity.QStudyGroupParticipant;
import com.sjy.LitHub.record.entity.StudyGroupStatus;
import com.sjy.LitHub.record.model.NotificationResponseDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StudyGroupRepositoryImpl implements StudyGroupRepositoryCustom {

	private final JPAQueryFactory qf;

	private final QStudyGroup g = QStudyGroup.studyGroup;
	private final QStudyGroupParticipant p = QStudyGroupParticipant.studyGroupParticipant;
	private final QUser u = QUser.user;
	private final QUser ou = new QUser("owner");

	@Override
	public List<StudyGroupHistoryDTO> findRecentEndedWithMembersByUser(Long userId, int limit) {
		QUserGenFile ugf256 = new QUserGenFile("ugf256");
		QUserGenFile ugf256p = new QUserGenFile("ugf256p");

		return qf.from(g)
			.join(p).on(p.group.eq(g))
			.join(g.owner, ou)
			.join(p.user, u)
			.leftJoin(ugf256).on(ugf256.user.eq(ou).and(ugf256.typeCode.eq(UserGenFile.TypeCode.PROFILE_256)))
			.leftJoin(ugf256p).on(ugf256p.user.eq(u).and(ugf256p.typeCode.eq(UserGenFile.TypeCode.PROFILE_256)))
			.where(
				p.user.id.eq(userId),
				g.status.eq(StudyGroupStatus.ENDED)
			)
			.orderBy(g.updatedAt.desc())
			.limit(limit)
			.transform(GroupBy.groupBy(g.id).list(
				Projections.constructor(
					StudyGroupHistoryDTO.class,
					g.title,
					g.content,
					g.totalMinutes,
					g.createdAt,
					Projections.constructor(
						UserBriefDTO.class,
						ou.id,
						ou.nickName,
						ugf256.storageKey
					),
					GroupBy.list(
						Projections.constructor(
							UserBriefDTO.class,
							u.id,
							u.nickName,
							ugf256p.storageKey
						)
					)
				)
			));
	}

	@Override
	public Optional<NotificationResponseDTO> findInviteNotificationByGroupId(Long roomId) {
		NotificationResponseDTO dto = qf
			.select(Projections.constructor(
				NotificationResponseDTO.class,
				g.title,
				g.owner.nickName,
				g.id
			))
			.from(g)
			.join(g.owner, u)
			.where(g.id.eq(roomId))
			.fetchOne();

		return Optional.ofNullable(dto);
	}
}