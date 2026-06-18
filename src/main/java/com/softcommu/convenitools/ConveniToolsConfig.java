package com.softcommu.convenitools;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.context.annotation.Configuration;

@Push // これをつけておくと getUI().access()メソッドでプッシュ通知ができます。Vaadin 23以降では、AppShellConfigurator を実装したクラスに @Push アノテーションをつける必要があります。Viewクラスに @Push をつけてもプッシュ通知はできません。
@Configuration
@StyleSheet(Lumo.STYLESHEET)
public class ConveniToolsConfig implements AppShellConfigurator {
    
}
