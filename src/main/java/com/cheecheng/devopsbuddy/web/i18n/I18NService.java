package com.cheecheng.devopsbuddy.web.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class I18NService {

    /* See
        http://www.petrikainulainen.net/software-development/design/why-i-changed-my-mind-about-field-injection/
        http://olivergierke.de/2013/11/why-field-injection-is-evil/
     */

    // ReloadableResourceBundleMessageSource is MessageSource. Obtain from I18NConfig
    private final MessageSource messageSource;
    // ReloadableResourceBundleMessageSource -> AbstractResourceBasedMessageSource ->
    // AbstractMessageSource -> HierarchicalMessageSource -> MessageSource

    @Autowired
    public I18NService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Return a message for the given message id and the default locale in the session context.
     *
     * @param messageId The key to the messages resource file
     * @return
     */
    public String getMessage(String messageId) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(messageId, locale);
    }

    /**
     * Return a message for the given message id and locale.
     *
     * @param messageId The key to the message resource file
     * @param locale    the locale
     * @return
     */
    public String getMessage(String messageId, Locale locale) {
        return messageSource.getMessage(messageId, null, locale);
    }
}
