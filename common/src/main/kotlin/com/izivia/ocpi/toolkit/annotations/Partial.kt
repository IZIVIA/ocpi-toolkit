package com.izivia.ocpi.toolkit.annotations

@Target(AnnotationTarget.CLASS)
// It defines the lifecycle of the annotation, we do not need it
// after compile, so we set it to source
@Retention(AnnotationRetention.SOURCE)
annotation class Partial
