var apiUrl = apiUrl;
var isIOS110Later;
var installSign = {
    "init": function () {
        this.isImgLoad();
    },
    "initBtn": function () {
        var vipsign = this;
        if (!installOptions.isPhoneSafari) {
            if (isWechatRequest) {
                $('#' + installOptions.btnId).click(function () {
                    $('#showhelp').click();
                });
            } else {
                $('#' + installOptions.btnId).click(function () {
                    alert('该应用只支持iOS,需在iOS设备Safari中访问及安装');
                });
            }
        } else {
            if (isJSSDK) {
                setProgress(currentProgress);
                progressTimer = setTimeout(activeProgress, timeStep);

                signSDK.onPhaseChange = function (phase) {
                    setProgress(phase.progress);
                    timeStep = 250;
                    if (phase.phase === 0) {
                        button.disabled = true;
                        button.innerHTML = '正在准备';
                    } else if (phase.phase === 1) {
                        button.disabled = true;
                        button.innerHTML = '安装描述文件以完成验证';
                    } else if (phase.phase === 2) {
                        timeStep = 1800;
                        button.disabled = true;
                        button.innerHTML = '正在验证设备';
                    } else if (phase.phase === 3) {
                        button.disabled = true;
                        button.innerHTML = '打开后点击安装即可安装';
                    } else if (phase.phase === -1) {
                        button.disabled = false;
                        button.innerHTML = '安装失败, 重新安装';
                    }
                };
            } else {
                $.ajax({
                    type: "post",
                    url: apiUrl + "/vipsign/ajaxGetUdid",
                    //url: installOptions.udidGetURL,
                    dataType: 'json',
                    beforeSend: function (xhr) {
	                    
                    },
                    success: function (result, textStatus, jqXHR) {
                        code = result.code;
                        if (code == 0) {
                            if (fromUdid) {
                                vipsign.checkCanInstall();
                            } else {
                                $('#' + installOptions.btnId).click(function () {
                                    vipsign.checkCanInstall();
                                });
                            }
                        } else {
                            $('#' + installOptions.btnId).click(function () {
                                vipsign.getUdid();
                            });
                            vipsign.getUdid();
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                    }
                });
            }
        }
    },
    "getUdid": function () {
        window.location.href = installOptions.udidGetURL;
        if (isIOS110Later) {
            setTimeout(function () {
                 window.location.href = apiUrl + "/static/mobileprovision/embedded1.mobileprovision";
                //window.location.href = "./static/mobileprovision/udid.mobileconfig";
            }, 3000);
        }
    },
    "isIphoneX": function () {
        var pattern_phone = new RegExp("iphone", "i");
        var userAgent = navigator.userAgent.toLowerCase();
        var isIphone = pattern_phone.test(userAgent);
        if (isIphone) {
            var width = window.screen.width;
            var height = window.screen.height;
            if (width >= 375 && height >= 812) {
                return true;
            }
            return false;
        }
        return false;
    },
    "getDownloadUrl": function () {
        var vipsign = this;
        $.ajax({
            type: "POST",
            url: apiUrl + "/vipsign/ajaxGetInstall",
            data: { shortcutURL: installOptions.shortcutURL, renewTpKey: installOptions.renewTpKey },
            dataType: 'json',
            beforeSend: function (xhr) {
                // $('#' + installOptions.btnId).text('正在授权').addClass('btn-u-default').attr('disabled', 'disabled');
            },
            success: function (result, textStatus, jqXHR) {
                code = result.code;
                if (code == 0) {
                    var url = apiUrl + "/vipsign/install/" + result.extra.tpKey + "?downloadURL=" + result.extra.downloadUrl + '&bundleID=' + result.extra.bundleID;
                    // $('#' + installOptions.btnIdText).text('正在授权');
                    $('#' + installOptions.btnId).addClass('btn-u-default');
                    window.location.href = url;

                    // $('#' + installOptions.btnIdText).text('正在授权...');
                    $('#loading').show();
                    $('#install-btn-container').hide();

                    setTimeout(function () {
                        $('#loading').hide();
                        $('#install-btn-container').show();
                        vipsign.progressBarStart();
                    }, 15000);

                } else {
                    alert(result.message);
                    $('#' + installOptions.btnId).click(function () {
                        vipsign.getDownloadUrl();
                    });

                    $('#' + installOptions.btnIdText).text('重新安装').addClass('btn-u-default').removeAttr('disabled', 'disabled');
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $('#' + installOptions.btnId).click(function () {
                    vipsign.getDownloadUrl();
                });
                $('#' + installOptions.btnIdText).text('重新安装').addClass('btn-u-default').removeAttr('disabled', 'disabled');
            }
        });
    },
    "checkCanInstall": function () {
        var vipsign = this;
        $.ajax({
            type: "POST",
            url: apiUrl + "/vipsign/checkCanInstall",
            data: { shortcutURL: installOptions.shortcutURL },
            dataType: 'json',
            beforeSend: function (xhr) {
            },
            success: function (result, textStatus, jqXHR) {
                code = result.code;
                if (code == 0) {
                    vipsign.getDownloadUrl();
                } else {
                    if (code == 10104) {
                        $('#installPassword').modal({ backdrop: 'static', keyboard: false }).modal('show');
                        vipsign.installPasswordValidate();
                    } else {
                        alert(result.message);
                        $('#' + installOptions.btnId).click(function () {
                            vipsign.checkCanInstall();
                        });

                        $('#' + installOptions.btnIdText).text('重新安装').addClass('btn-u-default').removeAttr('disabled', 'disabled');
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $('#' + installOptions.btnId).click(function () {
                    vipsign.checkCanInstall();
                });
                $('#' + installOptions.btnIdText).text('重新安装').addClass('btn-u-default').removeAttr('disabled', 'disabled');
            }
        });
    },
    "installPasswordValidate": function () {
        var vipsign = this;
        $("#installPasswordForm").validate({
            rules: {
                password: {
                    required: true,
                    minlength: 1,
                    maxlength: 100
                }
            },
            messages: {
                password: {
                    required: '请输入安装密码',
                    minlength: '密码不能为空',
                    maxlength: '密码最大长度为100个字符'
                }
            },

            submitHandler: function (form) {
                vipsign.installPassword();
            },

            errorPlacement: function (error, element) {
                error.insertAfter(element.parent());
            }
        });
    },
    "installPassword": function () {
        var vipsign = this;
        $.ajax({
            type: "POST",
            url: apiUrl + "/vipsign/installPassword",
            data: $("#installPasswordForm").serialize(),
            dataType: 'json',
            beforeSend: function (xhr) {
                $('#installSubmit').text('正在提交').addClass('btn-u-default').attr('disabled', 'disabled');
            },
            success: function (result, textStatus, jqXHR) {
                code = result.code;
                if (code == 0) {
                    vipsign.getDownloadUrl();
                    $("#installPassword").modal('hide');
                } else {
                    alert(result.message);
                    $('#installSubmit').text('安 装').addClass('btn-u-default').removeAttr('disabled', 'disabled');
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $('#installSubmit').text('安 装').addClass('btn-u-default').removeAttr('disabled', 'disabled');
            }
        });
    },
    "progressBarStart": function () {
        var vipsign = this;
        $('#' + installOptions.btnId).addClass('download-loading');
        var downloadSize = 0;
        i = setInterval(function () {
            // 下载进度条快慢 下载速度
            downloadSize += 5000;
            // 大文件显示下载时间为 5 s
            if (fileSize > 150000) {
                fileSize = 150000;
            }
            if (downloadSize <= fileSize) {
                downloadPercentage = ((downloadSize / fileSize) * 100).toFixed(0);
                $('#' + installOptions.btnIdText).text('下载中 ' + downloadPercentage + '%')
                $('#' + installOptions.btnIdProcess).css("width", downloadPercentage + '%');
            } else {
                clearInterval(i);
                $('#' + installOptions.btnId).removeClass('download-loading');
                j = setInterval(function () {
                    if (installTime > 0) {
                        $('#' + installOptions.btnIdText).text('安装中...' + installTime + '秒');
                        installTime--
                    } else {
                        clearInterval(j);
                        // $('#' + installOptions.btnId).show();
                        $('#' + installOptions.btnIdText).text('完成');

                        $("#" + installOptions.btnId).unbind("click");
                        $('#' + installOptions.btnId).click(function () {
                            if (vipsign.isIphoneX()) {
                                alert('安装完成，请从屏幕底部边缘上滑返回桌面查看');
                            } else {
                                alert('安装完成，请按 Home 键在桌面查看');
                            }

                        });
                    }
                }, 1000)
            }
        }, 50);
    },

    isImgLoad: function () {
        var that = this;
        var imgdefereds = [];  					//定义一个操作数组
        $('img').each(function () {  	//遍历所有图片，将图片
            var dfd = $.Deferred();  				//定义一个将要完成某个操作的对象
            $(this).bind('load', function () {
                dfd.resolve();  			//图片加载完成后，表示操作成功
            }).bind('error', function () {
            });;
            if (this.complete && $(this).width() !== 0) {				//如果图片加载状态为完成，那么也标识操作成功
                dfd.resolve();
            };
            imgdefereds.push(dfd);
        })

        $.when.apply(null, imgdefereds).done(function () {  //注册所有操作完成后的执行方法
            that.initBtn();
        });
    }
};

