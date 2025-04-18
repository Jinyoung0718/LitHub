package com.sjy.LitHub.post.repository.tag;

import java.util.List;
import java.util.Map;

public interface PostTagRepositoryCustom {
	Map<Long, List<String>> findTagNamesMap(List<Long> postIds);
}