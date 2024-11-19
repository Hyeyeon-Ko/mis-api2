package utils;

public class SearchUtils {
    public static boolean matchesSearchCriteria(String searchType, String keyword, String title, String drafter) {
        if (searchType == null || keyword == null) {
            return true;
        }

        return switch (searchType) {
            case "전체" -> (title.contains(keyword)) || (drafter.contains(keyword));
            case "제목" -> title.contains(keyword);
            case "신청자" -> drafter.contains(keyword);
            default -> true;
        };
    }
}
