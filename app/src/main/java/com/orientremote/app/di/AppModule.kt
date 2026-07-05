package com.orientremote.app.di

import com.orientremote.app.data.repository.RemoteRepository
import com.orientremote.app.data.repository.RemoteRepositoryImpl
import com.orientremote.app.ir.ConsumerIrTransmitter
import com.orientremote.app.ir.IrTransmitter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindIrTransmitter(impl: ConsumerIrTransmitter): IrTransmitter

    @Binds
    @Singleton
    abstract fun bindRemoteRepository(impl: RemoteRepositoryImpl): RemoteRepository
}
