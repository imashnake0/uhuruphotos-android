// Generated by Dagger (https://dagger.dev).
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.domain.usecase;

import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.usecase.LocalMediaUseCase;
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase;
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase;
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase;
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer;
import com.savvasdalkitsis.uhuruphotos.implementation.media.remote.repository.RemoteMediaRepository;
import com.savvasdalkitsis.uhuruphotos.implementation.media.remote.worker.RemoteMediaItemWorkScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class MediaUseCase_Factory implements Factory<MediaUseCase> {
  private final Provider<LocalMediaUseCase> localMediaUseCaseProvider;

  private final Provider<RemoteMediaUseCase> remoteMediaUseCaseProvider;

  private final Provider<RemoteMediaRepository> remoteMediaRepositoryProvider;

  private final Provider<RemoteMediaItemWorkScheduler> remoteMediaItemWorkSchedulerProvider;

  private final Provider<UserUseCase> userUseCaseProvider;

  private final Provider<DateDisplayer> dateDisplayerProvider;

  private final Provider<PeopleUseCase> peopleUseCaseProvider;

  public MediaUseCase_Factory(Provider<LocalMediaUseCase> localMediaUseCaseProvider,
      Provider<RemoteMediaUseCase> remoteMediaUseCaseProvider,
      Provider<RemoteMediaRepository> remoteMediaRepositoryProvider,
      Provider<RemoteMediaItemWorkScheduler> remoteMediaItemWorkSchedulerProvider,
      Provider<UserUseCase> userUseCaseProvider, Provider<DateDisplayer> dateDisplayerProvider,
      Provider<PeopleUseCase> peopleUseCaseProvider) {
    this.localMediaUseCaseProvider = localMediaUseCaseProvider;
    this.remoteMediaUseCaseProvider = remoteMediaUseCaseProvider;
    this.remoteMediaRepositoryProvider = remoteMediaRepositoryProvider;
    this.remoteMediaItemWorkSchedulerProvider = remoteMediaItemWorkSchedulerProvider;
    this.userUseCaseProvider = userUseCaseProvider;
    this.dateDisplayerProvider = dateDisplayerProvider;
    this.peopleUseCaseProvider = peopleUseCaseProvider;
  }

  @Override
  public MediaUseCase get() {
    return newInstance(localMediaUseCaseProvider.get(), remoteMediaUseCaseProvider.get(), remoteMediaRepositoryProvider.get(), remoteMediaItemWorkSchedulerProvider.get(), userUseCaseProvider.get(), dateDisplayerProvider.get(), peopleUseCaseProvider.get());
  }

  public static MediaUseCase_Factory create(Provider<LocalMediaUseCase> localMediaUseCaseProvider,
      Provider<RemoteMediaUseCase> remoteMediaUseCaseProvider,
      Provider<RemoteMediaRepository> remoteMediaRepositoryProvider,
      Provider<RemoteMediaItemWorkScheduler> remoteMediaItemWorkSchedulerProvider,
      Provider<UserUseCase> userUseCaseProvider, Provider<DateDisplayer> dateDisplayerProvider,
      Provider<PeopleUseCase> peopleUseCaseProvider) {
    return new MediaUseCase_Factory(localMediaUseCaseProvider, remoteMediaUseCaseProvider, remoteMediaRepositoryProvider, remoteMediaItemWorkSchedulerProvider, userUseCaseProvider, dateDisplayerProvider, peopleUseCaseProvider);
  }

  public static MediaUseCase newInstance(LocalMediaUseCase localMediaUseCase,
      RemoteMediaUseCase remoteMediaUseCase, RemoteMediaRepository remoteMediaRepository,
      RemoteMediaItemWorkScheduler remoteMediaItemWorkScheduler, UserUseCase userUseCase,
      DateDisplayer dateDisplayer, PeopleUseCase peopleUseCase) {
    return new MediaUseCase(localMediaUseCase, remoteMediaUseCase, remoteMediaRepository, remoteMediaItemWorkScheduler, userUseCase, dateDisplayer, peopleUseCase);
  }
}