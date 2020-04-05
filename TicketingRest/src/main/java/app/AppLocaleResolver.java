package app;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

public class AppLocaleResolver extends CookieLocaleResolver {

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		if (StringUtils.isEmpty(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE))) {
			return Locale.getDefault();
		}
		List<Locale.LanguageRange> list = Locale.LanguageRange.parse(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
		Locale locale = Locale.lookup(list, Arrays.asList(new Locale("en"), new Locale("tr")));
		return locale;
	}
}
